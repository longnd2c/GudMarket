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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gudmarket.web.entity.Account;
import com.gudmarket.web.entity.Post;
import com.gudmarket.web.entity.SocialAccount;
import com.gudmarket.web.google.GooglePojo;
import com.gudmarket.web.google.GoogleUtils;
import com.gudmarket.web.repository.AccountRepository;
import com.gudmarket.web.repository.PostRepository;
import com.gudmarket.web.repository.SocialAccountRepository;
import com.gudmarket.web.service.UserService;
import com.gudmarket.web.utils.WebUtils;




@Controller
public class MainController {
	
	@Autowired
	  private GoogleUtils googleUtils;
	@Autowired
	  private SocialAccountRepository socialAccRepo;
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
			  String username=loginedUser.getUsername();
			  Date time_now = new Date();
			  if(username.contains("@")) {
					SocialAccount socialAcc=socialAccRepo.findByEmail(username);
					Date level_to = socialAcc.getLevel_to();
					if(level_to!=null) {
						if(level_to.before(time_now)) {
							socialAcc.setLevel_to(null);
						}
						else {
							if(time_now.getDate()==level_to.getDate()) {
								switch(socialAcc.getId_level()) {
								case "L01":
									socialAcc.setPost_remain(socialAcc.getPost_remain()+30);
									break;
								case "L02":
									socialAcc.setPost_remain(socialAcc.getPost_remain()+60);
									break;
								case "L03":
									socialAcc.setPost_remain(socialAcc.getPost_remain()+120);
									break;
								}
							}
						}
						
					}
					socialAccRepo.save(socialAcc);
				}
				else {
					Account acc=accRepo.findByUsername(username);
					Date level_to=acc.getLevel_to();
					if(acc.getLevel_to()!=null) {
						if(acc.getLevel_to().before(time_now)) {
							acc.setLevel_to(null);
							
						}
						else {
							if(time_now.getDate()==level_to.getDate()) {
								switch(acc.getId_level()) {
								case "L01":
									acc.setPost_remain(acc.getPost_remain()+30);
									break;
								case "L02":
									acc.setPost_remain(acc.getPost_remain()+60);
									break;
								case "L03":
									acc.setPost_remain(acc.getPost_remain()+120);
									break;
								}
							}
						}
					}
					accRepo.save(acc);
				}
		}
		catch(Exception e){
			return "index";
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_SELLER")) {
				service.checkUser(model, principal);
				return "index";
			}
		}
		model.addAttribute("num_post", postRepo.findByStatus().size());
		return "consolePage";
		
    }
	
	@RequestMapping(value = "/login" , method = RequestMethod.GET)
    public String loginPage(Model model, Principal principal, Authentication authentication) {
		try {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
		}
		catch(Exception e){
			return "loginPage";
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_SELLER")) {
				service.checkUser(model, principal);
				return "index";
			}
		}
		return "consolePage";
    }
	
	@RequestMapping("/login-google")
	  public String loginGoogle(HttpServletRequest request) throws ClientProtocolException, IOException {
	    String code = request.getParameter("code");
	    
	    if (code == null || code.isEmpty()) {
	      return "redirect:/404";
	    }
	    String accessToken = googleUtils.getToken(code);
	    GooglePojo googlePojo = googleUtils.getUserInfo(accessToken);
	    SocialAccount socialAcc=googleUtils.saveSocialAccount(googlePojo);
	    
	    UserDetails userDetail = googleUtils.buildUser(socialAcc);
	    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
	        userDetail.getAuthorities());
	    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    return "redirect:/index";
	  }

	@RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model model, Principal principal, Authentication authentication) {
		try {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
		}
		catch(Exception e){
			model.addAttribute("user", new Account());
	        return "register";
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_SELLER")) {
				service.checkUser(model, principal);
				return "index";
			}
		}
		return "consolePage";
    }
	
	@RequestMapping(value = "/forgotpassword", method = RequestMethod.GET)
    public String forgotpassword(Model model, Principal principal, Authentication authentication) {
		try {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
		}
		catch(Exception e){
			model.addAttribute("user", new Account());
	        return "forgotPassword";
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_SELLER")) {
				service.checkUser(model, principal);
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
