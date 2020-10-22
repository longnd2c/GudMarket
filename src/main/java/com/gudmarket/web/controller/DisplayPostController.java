package com.gudmarket.web.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.gudmarket.web.entity.Post;
import com.gudmarket.web.repository.PostRepository;
import com.gudmarket.web.service.UserService;

@Controller
public class DisplayPostController {
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date - dd/MM/yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	@Autowired
	private PostRepository postRepo;
	@Autowired
	private UserService service;
	
	@RequestMapping("/allPost")
	  public String allPost(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			model.addAttribute("listPost",postRepo.findAllByPriorityDate());
			return "allPost";
		}
		model.addAttribute("listPost",postRepo.findAllByPriorityDate());
      return "allPost";
	  }
	@RequestMapping("/postdetail")
    public String postDetail(final HttpServletRequest request, Principal principal, Model model, @RequestParam("id_post" ) String id) {
		Post post=postRepo.findByPostId(id);
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			model.addAttribute("post", post);
			return "postDetail";
		}
		model.addAttribute("post", post);
        return "postDetail";
    }
	@RequestMapping("/allSellerPost")
	  public String allSellerPost(Model model, Principal principal) {
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username=loginedUser.getUsername();
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			model.addAttribute("listPost",postRepo.findSellerAllPost(username));
			return "allSellerPost";
		}
		model.addAttribute("listPost",postRepo.findSellerAllPost(username));
    return "allSellerPost";
	  }
	
	@RequestMapping("/sellerAllPost")
	  public String sellerAllPost(Model model, @RequestParam("username") String username) {
		model.addAttribute("listPost",postRepo.findSellerAllPost(username));
		model.addAttribute("username", username.split("@")[0]);
		return "sellerAllPost";
	  }
	
	@RequestMapping("/searchAllPost")
	  public String searchAllPost(@RequestParam(value = "searchKey") String searchKey, Model model, Principal principal) {
		List<Post> listPost = new ArrayList<Post>();
		try {
			for(Post post:postRepo.findAllByPriorityDate()) {
				if(post.toString().contains(searchKey)) {
					listPost.add(post);
				}
			}
			model.addAttribute("listPost", listPost);
			service.checkUser(model, principal);
		}
		catch(Exception e){
			model.addAttribute("listPost",listPost);
			return "allPost";
		}    
	    return "allPost";
	  }
	
	@RequestMapping("/filterAllPost")
	  public String filterAllPost(@RequestParam(value = "type") String type, @RequestParam(value = "sort") String sort,
			  					@RequestParam(value = "rangePrimary") String range , Model model, Principal principal) {
		try {
			List<Post> listPost = new ArrayList<Post>();
			String []price=range.split(";");
			String minPrice=price[0];
			String maxPrice=price[1];
			if (maxPrice.equals("1000")) {
				maxPrice="9999999999999999";
			}
			if(type.equals("all")) {
				switch(sort) {
				case "default":
					listPost=postRepo.findFilter(minPrice, maxPrice);
					break;
				case "new":
					listPost=postRepo.findFilter(minPrice, maxPrice);
					break;
				case "low":
					listPost=postRepo.findFilterPriceASC(minPrice, maxPrice);
					break;
				case "high":
					listPost=postRepo.findFilterPriceDESC(minPrice, maxPrice);
					break;
				}
			}
			else {
				switch(sort) {
				case "default":
					listPost=postRepo.findFilterType(type, minPrice, maxPrice);
					break;
				case "new":
					listPost=postRepo.findFilterType(type, minPrice, maxPrice);
					break;
				case "low":
					listPost=postRepo.findFilterTypePriceASC(type, minPrice, maxPrice);
					break;
				case "high":
					listPost=postRepo.findFilterTypePriceDESC(type, minPrice, maxPrice);
					break;
				}
			}		
			model.addAttribute("listPost", listPost);
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "allPost";
		}    
	    return "allPost";
	  }
}
