package com.gudmarket.web.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

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
			service.checkUser(model, principal);
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
