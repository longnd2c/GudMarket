package com.gudmarket.web.controller;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.gudmarket.web.entity.Account;
import com.gudmarket.web.phone.SendSMS;
import com.gudmarket.web.registration.OnRegistrationCompleteEvent;
import com.gudmarket.web.registration.PasswordResetCompleteEvent;
import com.gudmarket.web.repository.AccountRepository;
import com.gudmarket.web.service.UserService;
import com.gudmarket.web.utils.EncrytedPasswordUtils;
import com.twilio.Twilio;

@Controller
public class RegistrationController {
	
	@Autowired
	private AccountRepository accRepo;
	@Autowired
	private UserService service;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private SendSMS sendSms;
	
	@Autowired
    public void SMSController(
        @Value("${twilioAccountSid}") String twilioAccountSid,
        @Value("${twilioAuthToken}") String twilioAuthToken) {
        Twilio.init(twilioAccountSid, twilioAuthToken);
    }
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date - dd/MM/yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
	
	@RequestMapping("/registrationEmail") 
	  public ModelAndView doReg(@ModelAttribute("user") Account user, BindingResult bindingResult, ModelMap model,
			  			@RequestParam(value ="password") String password, 
			  			@RequestParam(value ="email") String email, HttpServletRequest request) { 
		  ModelAndView modelAndView=new ModelAndView();
		  Account accExists = accRepo.findByEmailOrPhone(email);
	        if (accExists != null) {
	        	bindingResult.rejectValue("email", "error.email","Email already exists.");
	        }
	        if (bindingResult.hasErrors()) {
	            modelAndView.setViewName("register");
	        }
	        else {
	        	service.saveUser(user, password);
			    String appUrl = "http://localhost:8080";
			    eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
			    model.addAttribute("email", email);
		        return new ModelAndView("redirect:/successRegister", model);
	        }
	  	
	  	return modelAndView; 
	  }
	
	@GetMapping("/successRegister")
    public ModelAndView successReg(final HttpServletRequest request, ModelMap model, @RequestParam("email" ) String email) {
		model.addAttribute("email", email);
        return new ModelAndView("successRegister", model);
    }
	
	@GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(HttpServletRequest request, ModelMap model, @RequestParam("token") String token) throws UnsupportedEncodingException {
        //Locale locale = request.getLocale();
        //model.addAttribute("lang", locale.getLanguage());
        final String result = service.validateVerificationToken(token);
        if (result.equals("valid")) {
            final Account user = service.getUser(token);
            // if (user.isUsing2FA()) {
            // model.addAttribute("qr", userService.generateQRUrl(user));
            // return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            // }
            //model.addAttribute("messageKey", "message.accountVerified");
        	UserDetails userDetail=service.buildUser(user);
    	    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
    	        userDetail.getAuthorities());
    	    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    	    SecurityContextHolder.getContext().setAuthentication(authentication);
    	    
        	service.deleteToken(token);
        	String message="Your Account was create successfully!";
        	model.addAttribute("message", message);
            return new ModelAndView("successVerification", model);
        }

//        model.addAttribute("messageKey", "auth.message." + result);
//        model.addAttribute("expired", "expired".equals(result));
//        model.addAttribute("token", token);
        return new ModelAndView("redirect:/404");
    }
	
	
	@RequestMapping("/registrationPhone") 
	  public ModelAndView doRegPhone(@ModelAttribute("user") Account user, BindingResult bindingResult, ModelMap model,
			  			@RequestParam(value ="password") String password, 
			  			@RequestParam(value ="phone") String phone, HttpServletRequest request) { 
		  ModelAndView modelAndView=new ModelAndView();
		  Account accExists = accRepo.findByEmailOrPhone(phone);
		  if(accExists==null) {
			  service.saveUser(user, password);
	        	sendSms.sendMessages(phone,user);
			    model.addAttribute("phone", phone);
			    model.addAttribute("user", user);
		        return new ModelAndView("validatePhone", model);
		  }
		  if(accExists!=null && accExists.isEnabled()==false) {
			  sendSms.sendMessages(phone,accRepo.findByEmailOrPhone(phone));
			    model.addAttribute("phone", phone);
			    model.addAttribute("user", accRepo.findByEmailOrPhone(phone));
		        return new ModelAndView("validatePhone", model);
		  }
	        if (accExists!=null && accExists.isEnabled()==true) {
	        	bindingResult.rejectValue("phone", "error.phone","Phone number already exists.");
	        }
	        if (bindingResult.hasErrors()) {
	            modelAndView.setViewName("registerPhone");
	        }
	  	return modelAndView; 
	  }
	@RequestMapping("/doValidatePhone") 
	  public ModelAndView doValidatePhone(HttpServletRequest request, @ModelAttribute("user") Account user, BindingResult bindingResult, ModelMap model,
			  			@RequestParam(value ="code") String code, @RequestParam(value ="phone") String phone) { 
		  ModelAndView modelAndView=new ModelAndView();
		  String result=service.validateVerificationToken(code);
	        if (!result.equals("valid")) {
	        	bindingResult.rejectValue("email", "error.email","Validate Code is incorrect!");
	        }
	        if (bindingResult.hasErrors()) {
	        	modelAndView.addObject("phone", phone );
	            modelAndView.setViewName("validatePhone");
	        }
	        else {
	        	final Account _user = service.getUser(code);
	        	UserDetails userDetail=service.buildUser(_user);
	    	    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
	    	        userDetail.getAuthorities());
	    	    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	    	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    	    service.deleteCode(_user);
	    	    String message="Your Phone Number verification was successful!";
	    	    model.addAttribute("message", message);
		        return new ModelAndView("successVerification");
	        }
	  	
	  	return modelAndView; 
	  }
	//////////////////FORGET PASSWORD////////////////
	@RequestMapping("/forgotpassaction") 
	  public ModelAndView forgotpass(@ModelAttribute("user") Account user, BindingResult bindingResult, ModelMap model, @RequestParam(value ="emailOrPhone") String emailOrPhone, HttpServletRequest request) { 
		  ModelAndView modelAndView=new ModelAndView();
		  Account accExists = accRepo.findByEmailOrPhone(emailOrPhone);
	        if (accExists == null) {
	        	bindingResult.rejectValue("email", "error.email","Email was not registered.");
	        }
	        if (bindingResult.hasErrors()) {
	            modelAndView.setViewName("forgotPassword");
	        }
	        else {
			    String appUrl = "http://localhost:8080";
			    eventPublisher.publishEvent(new PasswordResetCompleteEvent(accExists, request.getLocale(), appUrl));
			    model.addAttribute("emailOrPhone", emailOrPhone);
		        return new ModelAndView("redirect:/successForgot", model);
	        }
	  	
	  	return modelAndView; 
	  }
	
	@GetMapping("/successForgot")
    public ModelAndView successForgot(final HttpServletRequest request, ModelMap model, @RequestParam("emailOrPhone" ) String emailOrPhone) {
		model.addAttribute("emailOrPhone", emailOrPhone);
        return new ModelAndView("successForgot", model);
    }
	
	@GetMapping("/resetPassword")
    public ModelAndView resetPassword(HttpServletRequest request, ModelMap model, @RequestParam("token") String token) throws UnsupportedEncodingException {
        //Locale locale = request.getLocale();
        //model.addAttribute("lang", locale.getLanguage());
        final String result = service.validateResetPasswordToken(token);
        if (result.equals("valid")) {
            final Account user = service.getUser(token);
            // if (user.isUsing2FA()) {
            // model.addAttribute("qr", userService.generateQRUrl(user));
            // return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            // }
            //model.addAttribute("messageKey", "message.accountVerified");
    	    model.addAttribute("user", user);
    	    service.deleteToken(token);
            return new ModelAndView("resetPassword", model);
            
        }

//        model.addAttribute("messageKey", "auth.message." + result);
//        model.addAttribute("expired", "expired".equals(result));
//        model.addAttribute("token", token);
        return new ModelAndView("redirect:/404");
    }

	
	@RequestMapping("/doResetPassword") 
	  public ModelAndView doResetPass(@ModelAttribute("user") Account user, @RequestParam(value ="password") String password,
			  							BindingResult bindingResult, ModelMap model, HttpServletRequest request) { 
		ModelAndView modelAndView=new ModelAndView();
    	user.setPassword(EncrytedPasswordUtils.encrytePassword(password));
    	accRepo.save(user);
	  	modelAndView.setViewName("successResetPassword");
	  	return modelAndView; 
	  }
	
}
