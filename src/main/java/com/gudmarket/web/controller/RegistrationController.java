package com.gudmarket.web.controller;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.gudmarket.web.registration.OnRegistrationCompleteEvent;
import com.gudmarket.web.registration.PasswordResetCompleteEvent;
import com.gudmarket.web.repository.AccountRepository;
import com.gudmarket.web.service.UserService;
import com.gudmarket.web.utils.EncrytedPasswordUtils;

@Controller
public class RegistrationController {
	
	@Autowired
	private AccountRepository accRepo;
	@Autowired
	private UserService service;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date - dd/MM/yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
	
	@RequestMapping("/registration") 
	  public ModelAndView doReg(@ModelAttribute("user") Account user, BindingResult bindingResult, ModelMap model,
			  			@RequestParam(value = "username") String username, @RequestParam(value ="password") String password, 
			  			@RequestParam(value ="email") String email, HttpServletRequest request) { 
		  ModelAndView modelAndView=new ModelAndView();
		  Account accExists = accRepo.findByUsername(username);
		  Account accExists2 = accRepo.findByEmail(email);
	        if (accExists != null || accExists2 !=null) {
	        	bindingResult.rejectValue("username", "error.username","Username/email already exists.");
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
            return new ModelAndView("redirect:/successVerification");
        }

//        model.addAttribute("messageKey", "auth.message." + result);
//        model.addAttribute("expired", "expired".equals(result));
//        model.addAttribute("token", token);
        return new ModelAndView("redirect:/404");
    }
	
	@GetMapping("/successVerification")
    public ModelAndView successVer(final HttpServletRequest request) {
        return new ModelAndView("successVerification");
    }
	
	//////////////////FORGET PASSWORD////////////////
	@RequestMapping("/forgotpassaction") 
	  public ModelAndView forgotpass(@ModelAttribute("user") Account user, BindingResult bindingResult, ModelMap model, @RequestParam(value ="email") String email, HttpServletRequest request) { 
		  ModelAndView modelAndView=new ModelAndView();
		  Account accExists = accRepo.findByEmail(email);
	        if (accExists == null) {
	        	bindingResult.rejectValue("email", "error.email","Email was not registered.");
	        }
	        if (bindingResult.hasErrors()) {
	            modelAndView.setViewName("forgotPassword");
	        }
	        else {
			    String appUrl = "http://localhost:8080";
			    eventPublisher.publishEvent(new PasswordResetCompleteEvent(accExists, request.getLocale(), appUrl));
			    model.addAttribute("email", email);
		        return new ModelAndView("redirect:/successForgot", model);
	        }
	  	
	  	return modelAndView; 
	  }
	
	@GetMapping("/successForgot")
    public ModelAndView successForgot(final HttpServletRequest request, ModelMap model, @RequestParam("email" ) String email) {
		model.addAttribute("email", email);
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
