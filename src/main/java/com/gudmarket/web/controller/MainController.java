package com.gudmarket.web.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gudmarket.web.entity.Account;
import com.gudmarket.web.entity.Post;
import com.gudmarket.web.google.GooglePojo;
import com.gudmarket.web.google.GoogleUtils;
import com.gudmarket.web.repository.AccountRepository;
import com.gudmarket.web.repository.PostRepository;
import com.gudmarket.web.service.UserService;
import com.gudmarket.web.utils.WebUtils;




@Controller
public class MainController {
	
	@Autowired
	  private GoogleUtils googleUtils;
	@Autowired
	private AccountRepository accRepo;
	@Autowired
	private UserService service;
	@Autowired
	private PostRepository postRepo;

	@RequestMapping(value = {"/" ,"/index" }, method = RequestMethod.GET)
    public String homePage(Model model, Principal principal, Authentication authentication) {
		try {
			///////////////REMOVE PRIORITY////////////////
			List<Post> listPriority=postRepo.findPriority();
			Date now = new Date();
			Date priority=null;
			for(Post p : listPriority) {
				priority=p.getPriority();
				if(priority!=null && priority.before(now)) {
					p.setPriority(null);
					postRepo.save(p);
				}
			}
			/////////////DELETE 30 DAY POST///////////////
			List<Post> list=postRepo.findAll();
			Calendar c = Calendar.getInstance(); 
			c.setTime(now); 
			c.add(Calendar.DATE, -30);
			now = c.getTime();
			Date date=null;
			for(Post p : list) {
				date=p.getDate();
				if(priority!=null && date.before(now)) {
					postRepo.delete(p);
				}
			}
			 service.checkUser(model, principal);
			//////////////DO ADD POST TO ACCOUNT WITH LEVEL/////////////////////
			 User loginedUser = (User) ((Authentication) principal).getPrincipal();
			  String userId=loginedUser.getUsername();
			  System.out.print(loginedUser.getUsername());  
			  Date time_now = new Date();
					Account acc=accRepo.findByUserId(userId);
					Date last_impact =acc.getLast_impact();
					if(acc.getLevel_to()!=null) {
						if(acc.getLevel_to().before(time_now)) {
							acc.setLevel_to(null);
							
						}
						else {
							if(time_now.getDate()==last_impact.getDate() && time_now.getMonth()!=last_impact.getMonth()) {
								switch(acc.getId_level()) {
								case "L02":
									acc.setPost_remain(acc.getPost_remain()+30);
									acc.setLast_impact(time_now);
									break;
								case "L03":
									acc.setPost_remain(acc.getPost_remain()+60);
									acc.setLast_impact(time_now);
									break;
								case "L04":
									acc.setPost_remain(acc.getPost_remain()+120);
									acc.setLast_impact(time_now);
									break;
								}
							}
						}
					}
					accRepo.save(acc);
				
		}
		catch(Exception e){
			return "index";
		}
		 User loginedUser = (User) ((Authentication) principal).getPrincipal();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_SELLER")) {
				model.addAttribute("user", accRepo.findByUserId(loginedUser.getUsername()));
				return "index";
			}
		}
		model.addAttribute("num_post", postRepo.findByStatus().size());
		return "consolePage";
		
    }
	
	@RequestMapping(value = "/login" , method = RequestMethod.GET)
    public String loginPage(Model model, Principal principal, Authentication authentication) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "loginPage";
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_SELLER")) {
				return "index";
			}
		}
		return "consolePage";
    }
	
	@RequestMapping("/login-google")
	  public String loginGoogle(HttpServletRequest request, Model model) throws ClientProtocolException, IOException {
	    String code = request.getParameter("code");
	    
	    if (code == null || code.isEmpty()) {
	      return "redirect:/404";
	    }
	    String accessToken = googleUtils.getToken(code);
	    GooglePojo googlePojo = googleUtils.getUserInfo(accessToken);
	    Account acc=googleUtils.findAccount(googlePojo);
	    if(acc==null) {
	    	acc=googleUtils.saveAccount(googlePojo);
	    	model.addAttribute("user", acc);
	    	return "setPassword";
	    }    
	    UserDetails userDetail = googleUtils.buildUser(acc);
	    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
	        userDetail.getAuthorities());
	    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    return "redirect:/index";
	  }
	
	@RequestMapping(value = "/doSetPassword")
    public String setPass(Model model, Principal principal, HttpServletRequest request,
    						@RequestParam("password" ) String password, @ModelAttribute("user") Account user) {
		service.saveGoogle(user, password);
		final Account log_user = accRepo.findByUserId(user.getId_user());
		UserDetails userDetail = googleUtils.buildUser(log_user);
	    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
	        userDetail.getAuthorities());
	    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	    SecurityContextHolder.getContext().setAuthentication(authentication);
		return "redirect:/index";
	}
		
	@RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model model, Principal principal, Authentication authentication) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			model.addAttribute("user", new Account());
	        return "register";
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_SELLER")) {
				return "index";
			}
		}
		return "consolePage";
    }
	@RequestMapping(value = "/registerPhone", method = RequestMethod.GET)
    public String registerPhone(Model model, Principal principal, Authentication authentication) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			model.addAttribute("user", new Account());
	        return "registerPhone";
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_SELLER")) {
				return "index";
			}
		}
		return "consolePage";
    }
	
	@RequestMapping(value = "/forgotpassword", method = RequestMethod.GET)
    public String forgotpassword(Model model, Principal principal, Authentication authentication) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			model.addAttribute("user", new Account());
	        return "forgotPassword";
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_SELLER")) {
				return "index";
			}
		}
		return "consolePage";
    }
	
	@RequestMapping(value = "/consolePage", method = RequestMethod.GET)
    public String adminPage(Model model, Principal principal) {
		model.addAttribute("num_post", postRepo.findByStatus().size());
        return "consolePage";
    }
	
	

}
