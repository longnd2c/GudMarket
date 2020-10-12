package com.gudmarket.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HandleErrorController implements ErrorController {
	
	@RequestMapping("/error")
	  public String handleError(HttpServletRequest request) {
	    // do something
	    return "404Page";
	  }
	@RequestMapping("/403")
	  public String Error403(HttpServletRequest request) {
	    // do something
	    return "403Page";
	  }
	  @Override
	  public String getErrorPath() {
	    return "/error";
	  }
}
