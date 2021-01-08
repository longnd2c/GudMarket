package com.gudmarket.web.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gudmarket.web.entity.Account;
import com.gudmarket.web.entity.Post;
import com.gudmarket.web.phone.SendSMS;
import com.gudmarket.web.registration.OnChangeEmailCompleteEvent;
import com.gudmarket.web.registration.OnRegistrationCompleteEvent;
import com.gudmarket.web.repository.AccountRepository;
import com.gudmarket.web.repository.CategoryRepository;
import com.gudmarket.web.repository.PostRepository;
import com.gudmarket.web.repository.TypeRepository;
import com.gudmarket.web.service.PostService;
import com.gudmarket.web.service.UserService;
import com.gudmarket.web.utils.WebUtils;
import com.twilio.Twilio;

@Controller
public class SellerController {
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date - dd/MM/yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	@Autowired
	private UserService service;
	@Autowired
	private AccountRepository accRepo;
	@Autowired
	private PostRepository postRepo;
	@Autowired
	private TypeRepository typeRepo;
	@Autowired
	private PostService postService;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private CategoryRepository cateRepo;
	@Autowired
	private SendSMS sendSms;
	@Autowired
    public void SMSController(
        @Value("${twilioAccountSid}") String twilioAccountSid,
        @Value("${twilioAuthToken}") String twilioAuthToken) {
        Twilio.init(twilioAccountSid, twilioAuthToken);
    }
	
	@RequestMapping("/profile")
	  public String profile(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}

        return "profile";
	  }
	
	@RequestMapping("/updateProfile")
	  public String updateProfile(Model model, Principal principal,@RequestParam("fullName") String fullName, 
			 					@RequestParam("address") String address, @RequestParam("phone") String phone) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String userId=loginedUser.getUsername();
			Account acc=accRepo.findByUserId(userId);
			acc.setFull_name(fullName);
			acc.setAddress(address);
			acc.setPhone(phone);
			accRepo.save(acc);
      return "profile";
	  }
	
	@RequestMapping("/changePassword")
	  public String changePass(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
    
    return "changePassword";
	  }
	@RequestMapping("/changePhone")
	  public String changePhone(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
  
  return "changePhone";
	  }
	@RequestMapping("/changeEmail")
	  public String changeEmail(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
  
  return "changeEmail";
	  }
	
	@RequestMapping("/accountwallet")
	  public String accwallet(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
      
      return "accountWallet";
	  }
	
	@RequestMapping("/doChangePassword")
	  public String doChangePass(Model model, Principal principal, @RequestParam("currentPass") String currentPass, 
			  						@RequestParam("password") String password, RedirectAttributes redirectAttributes) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		  String userId=loginedUser.getUsername();
		  Account acc=accRepo.findByUserId(userId);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if(encoder.matches(currentPass, acc.getPassword())){
			acc.setPassword(encoder.encode(password));
			accRepo.save(acc);
			redirectAttributes.addFlashAttribute("message", "Change Password Successfully!");
			return "redirect:/changePassword";
		}
		else {
			redirectAttributes.addFlashAttribute("message1", "The current password entered is incorrect!");
			return "redirect:/changePassword";
		}
	  }
	@RequestMapping("/doChangeEmail")
	  public String doChangeEmail(Model model, Principal principal, @RequestParam("email") String email, 
			  RedirectAttributes redirectAttributes, HttpServletRequest request) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
		Account accExists = accRepo.findByEmailOrPhone(email);
		  User loginedUser = (User) ((Authentication) principal).getPrincipal();
		  String userId=loginedUser.getUsername();
		  Account user=accRepo.findByUserId(userId);
		  if(accExists==null) {
			  String appUrl = "http://localhost:8080";
			    eventPublisher.publishEvent(new OnChangeEmailCompleteEvent(user, request.getLocale(), appUrl, email));
			    redirectAttributes.addFlashAttribute("message", "An confirm email was sent to "+ email+". Please check your email!");
				  return "redirect:/changeEmail";
		  }
		  else {
			  redirectAttributes.addFlashAttribute("message1", "Email address already exists!");
			  return "redirect:/changeEmail";
	        }
	  }
	@GetMapping("/changeEmailConfirm")
    public String confirmRegistration(HttpServletRequest request, ModelMap model, @RequestParam("token") String token, 
    		@RequestParam("email") String email, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        final String result = service.validateVerificationToken(token);
        if (result.equals("valid")) {
            final Account user = service.getUser(token);
    	    user.setEmail(email);
    	    accRepo.save(user);
        	service.deleteToken(token);
        	redirectAttributes.addFlashAttribute("message", "Your Email Address was add successfully!");
			  return "redirect:/changeEmail";
        }
        return "redirect:/404";
    }
	
	@RequestMapping("/sendValidatePhone") 
	  public String sendValidate(ModelMap model, RedirectAttributes redirectAttributes,
			  			@RequestParam(value ="phone") String phone, HttpServletRequest request, Principal principal) { 
		  Account accExists = accRepo.findByEmailOrPhone(phone);
		  User loginedUser = (User) ((Authentication) principal).getPrincipal();
		  String userId=loginedUser.getUsername();
		  Account user=accRepo.findByUserId(userId);
		  if(accExists==null) {
	        	sendSms.sendMessages(phone,user);
			    model.addAttribute("phone", phone);
			    model.addAttribute("user", user);
		        return "sendValidatePhone";
		  }
		  else {
			  redirectAttributes.addFlashAttribute("message1", "Phone number already exists!");
			  return "redirect:/changePhone";
	        }
	  }
	@RequestMapping("/doChangePhone")
	  public ModelAndView doChangePhone(Model model, Principal principal, @RequestParam("code") String code, @ModelAttribute("user") Account user,
			  					@RequestParam("phone") String phone, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
		
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return new ModelAndView("redirect:/login");
		}
		ModelAndView modelAndView=new ModelAndView();
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String result=service.validateVerificationToken(code);
		if (!result.equals("valid")) {
        	bindingResult.rejectValue("email", "error.email","Validate Code is incorrect!");
        }
        if (bindingResult.hasErrors()) {
        	modelAndView.addObject("phone", phone );
        	modelAndView.addObject("message", "Validate Code is incorrect!" );
            modelAndView.setViewName("sendValidatePhone");
        }
		else {
			String userId=loginedUser.getUsername();
			  Account acc=accRepo.findByUserId(userId);
			  acc.setPhone(phone);
				accRepo.save(acc);
				service.deleteCode(acc);
				redirectAttributes.addFlashAttribute("message", "New Phone was Change/Add Successfully!");
				return new ModelAndView("redirect:/changePhone");
		}
        return modelAndView;
	  }
	
	@RequestMapping("/postNew")
	  public String postNew(Model model, Principal principal, RedirectAttributes redirectAttributes) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
		int post_remain=0;
		Date now= new Date();
		Date block= null;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		  String userId=loginedUser.getUsername();
				Account acc=accRepo.findByUserId(userId);
				post_remain=acc.getPost_remain();
				block=acc.getBlock_to();
			
		  if(post_remain<=0) {
			 redirectAttributes.addFlashAttribute("message", "You cannot post because you have 0 POST REMAINING in your account!");
			 return "redirect:/postManage";
		  }
		  if(block!=null && block.after(now)) {
			redirectAttributes.addFlashAttribute("message", "You cannot post because YOUR ACCOUNT WAS BLOCK TO "+block.toString());
			return "redirect:/postManage";
		  }
		model.addAttribute("post", new Post());
		model.addAttribute("listCategory", cateRepo.findAll());
		model.addAttribute("listType1", typeRepo.findByCategory("C01"));
		model.addAttribute("listType2", typeRepo.findByCategory("C02"));
		model.addAttribute("listType3", typeRepo.findByCategory("C03"));
		model.addAttribute("listType4", typeRepo.findByCategory("C04"));
		model.addAttribute("listType5", typeRepo.findByCategory("C05"));
		model.addAttribute("listType6", typeRepo.findByCategory("C06"));
		model.addAttribute("listType7", typeRepo.findByCategory("C07"));
		return "postNew";
	  }
	@RequestMapping("/doPost") 
	  public ModelAndView doPost(@ModelAttribute("post") Post post, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes,
			  						@RequestParam("upload_file[]") MultipartFile[] uploadingFiles, @RequestParam("choose_time") String priority_time) throws IOException { 
		  ModelAndView modelAndView=new ModelAndView();
		  User loginedUser = (User) ((Authentication) principal).getPrincipal();
		  String userId=loginedUser.getUsername();
			int post_remain=0;
			Double money=0.00;
			int date=0;
			if(!priority_time.equals("default")) {
				date=Integer.parseInt(priority_time);
			}
			
				Account acc=accRepo.findByUserId(userId);
				post_remain=acc.getPost_remain();
				money=acc.getMoney();
			
			if(post_remain>0 && date==0){
				post.setPriority(null);
				postService.savePost(post, principal, uploadingFiles, modelAndView);
			}
			if(post_remain>0 && date!=0) {
				if(money>date) {
						acc.setMoney(acc.getMoney()-date);
						accRepo.save(acc);
					
					Date dt = new Date();
					Calendar c = Calendar.getInstance(); 
					c.setTime(dt); 
					c.add(Calendar.DATE, date);
					dt = c.getTime();
					post.setPriority(dt);
					postService.savePost(post, principal, uploadingFiles, modelAndView);
				}
				else {
					redirectAttributes.addFlashAttribute("message", "You do not have enough money in your account to post!");
					modelAndView.setViewName("redirect:/postNew"); 
					return modelAndView;
				}
			}
			if(post_remain<=0) {
				redirectAttributes.addFlashAttribute("message", "Post remain numbers in account are not enough!");
				modelAndView.setViewName("redirect:/postNew"); 
				return modelAndView;
			}

	        	//modelAndView.addObject("listSeller",accRepo.findSeller());
			redirectAttributes.addFlashAttribute("message", "Post successfully!! Please wait until the administrator has approved your post!");
	       modelAndView.setViewName("redirect:/allSellerPost"); 
	       
	  	return modelAndView; 
	  }
	
	@RequestMapping("/seller")
	  public String sellerprofile(Model model, Principal principal, @RequestParam("id_user") String id_user) {
		try {
			service.checkUser(model, principal);
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			if(id_user==loginedUser.getUsername()) {
				return "redirect:/profile";
			}
			else {

					model.addAttribute("post_user",accRepo.findByUserId(id_user));
				model.addAttribute("listPost", postRepo.findSellerNewPost(id_user));
				model.addAttribute("userId", id_user);
			    return "seller";
			}
		}
		catch(Exception e){
			model.addAttribute("post_user",accRepo.findByUserId(id_user));
			model.addAttribute("listPost", postRepo.findSellerNewPost(id_user));
			model.addAttribute("userId", id_user);
		    return "seller";
		}
	  }
	
	@RequestMapping("/postManage")
	  public String postManage(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username=loginedUser.getUsername();
      model.addAttribute("listPost",postRepo.findSellerNewPost(username));
      return "postManage";
	  }
	
	@RequestMapping("/buyPost")
	  public String buyPost(Model model, Principal principal, RedirectAttributes redirectAttributes, @RequestParam("buy_money") String buyMoney, @RequestParam("num_post") String num_post) {
			Double m= Double.parseDouble(buyMoney);
			int num=Integer.parseInt(num_post);
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String userId=loginedUser.getUsername();
				Account acc=accRepo.findByUserId(userId);
				if(acc.getMoney()<m) {
					redirectAttributes.addFlashAttribute("message", "You don't have enough money in your account!");
					return "redirect:/postManage";
				}
				acc.setMoney(acc.getMoney()-m);
				acc.setPost_remain(acc.getPost_remain()+num);
				accRepo.save(acc);
			
		return "redirect:/postManage";
	  }
	
	@RequestMapping("/sellerPostDelete/{id}")
	  public String doDeletePost(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
		 postRepo.deleteById(id);
		 redirectAttributes.addFlashAttribute("message", "Delete Post Successfully!!");
	    return "redirect:/allSellerPost";
	  }
	
	/////////////////////UPGRADE/////////////////////////////////////
	@RequestMapping("/upgrade")
	  public String upgrade(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
	    return "upgrade";
	  }
	@RequestMapping("/upgradeSilver")
	  public String upgradeSilver(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
	    return "upgradeSilver";
	  }
	
	@RequestMapping("/doUpgradeSilver")
	  public String doUpgradeSilver(Model model, Principal principal, @RequestParam("buy_money") String money, @RequestParam("num_time") String num_time, RedirectAttributes redirectAttributes ) {
		Double m= Double.parseDouble(money);
		int num=Integer.parseInt(num_time);
		Date dt = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(dt); 
		c.add(Calendar.MONTH, num);
		dt = c.getTime();
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String userId=loginedUser.getUsername();
			Account acc=accRepo.findByUserId(userId);
			if(acc.getMoney()<m) {
				redirectAttributes.addFlashAttribute("message", "You don't have enough money in your account!");
				return "redirect:/upgradeSilver";
			}
			acc.setMoney(acc.getMoney()-m);
			acc.setId_level("L02");
			acc.setPost_remain(acc.getPost_remain()+30);
			acc.setLevel_to(dt);
			acc.setLast_impact(new Date());
			accRepo.save(acc);
		
		redirectAttributes.addFlashAttribute("message", "Upgrade account to SILVER level successfully!!");
	    return "redirect:/upgradeSilver";
	  }
	
	@RequestMapping("/upgradeGold")
	  public String upgradeGold(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
	    return "upgradeGold";
	  }
	@RequestMapping("/upgradePlatinum")
	  public String upgradePlatinum(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "login";
		}
	    return "upgradePlatinum";
	  }
}
