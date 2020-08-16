package com.gudmarket.web.controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gudmarket.web.utils.WebUtils;



@Controller
public class MainController {

	@RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
    public String loginPage(Model model) {
 
        return "loginPage";
    }
	
	@RequestMapping(value = "/home", method = RequestMethod.GET)
    public String adminPage(Model model, Principal principal) {
         
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
 
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
         
        return "homePage";
    }
	
	@RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
    public String logoutSuccessfulPage(Model model) {
        model.addAttribute("title", "Logout");
        return "logoutSuccessfulPage";
    }
	
	@RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied(Model model) {
        return "403Page";
    }
}
