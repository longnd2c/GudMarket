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
import com.gudmarket.web.repository.AccountRepository;
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
	@Autowired
	private AccountRepository accRepo;
	
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
	  public String sellerAllPost(Model model, @RequestParam("id_user") String id_user, Principal principal) {
		try {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String userId=loginedUser.getUsername();
			model.addAttribute("user", accRepo.findByUserId(userId));
			model.addAttribute("listPost",postRepo.findSellerAllPost(id_user));
			model.addAttribute("name", accRepo.findByUserId(id_user).getFull_name());
			return "sellerAllPost";
		}
		catch (Exception ex){
			model.addAttribute("listPost",postRepo.findSellerAllPost(id_user));
			model.addAttribute("name", accRepo.findByUserId(id_user).getFull_name());
			return "sellerAllPost";
		}
		
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
	
	@RequestMapping("/searchAllSellerPost")
	  public String searchAllSellerPost(@RequestParam(value = "searchKey") String searchKey,@RequestParam(value = "username") String username, Model model, Principal principal) {
		List<Post> listPost = new ArrayList<Post>();
		try {
			for(Post post:postRepo.findSellerAllPost(username)) {
				if(post.toString().contains(searchKey)) {
					listPost.add(post);
				}
			}
			model.addAttribute("listPost", listPost);
			service.checkUser(model, principal);
		}
		catch(Exception e){
			model.addAttribute("listPost",listPost);
			model.addAttribute("username", username);
			return "sellerAllPost";
		}    
	    return "allSellerPost";
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
	////////////TYPE//////////////////////
	@RequestMapping("/electronics")
	  public String allElectronics(Model model, Principal principal) {
		try {
			service.checkUser(model, principal);
		}
		catch(Exception e){
			model.addAttribute("listPost",postRepo.findByCateId("C02"));
			return "electronics";
		}
		model.addAttribute("listPost",postRepo.findByCateId("C02"));
		return "electronics";
	  }
	
	@RequestMapping("/searchElectronics")
	  public String searchElectronic(@RequestParam(value = "searchKey") String searchKey, Model model, Principal principal) {
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
			return "electronics";
		}    
	    return "electronics";
	  }
	
	@RequestMapping("/filterElectronics")
	  public String filterHomeKit(@RequestParam(value = "type") String type, @RequestParam(value = "sort") String sort,
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
				type="C02";
				switch(sort) {
				case "default":
					listPost=postRepo.findFilterST(type, minPrice, maxPrice);
					break;
				case "new":
					listPost=postRepo.findFilterST(type, minPrice, maxPrice);
					break;
				case "low":
					listPost=postRepo.findFilterSTPriceASC(type,minPrice, maxPrice) ;
					break;
				case "high":
					listPost=postRepo.findFilterSTPriceDESC(type, minPrice, maxPrice);
					break;
				}
			}
			else {
				switch(sort) {
				case "default":
					listPost=postRepo.findFilterSubType(type, minPrice, maxPrice);
					break;
				case "new":
					listPost=postRepo.findFilterSubType(type, minPrice, maxPrice);
					break;
				case "low":
					listPost=postRepo.findFilterSubTypePriceASC(type, minPrice, maxPrice);
					break;
				case "high":
					listPost=postRepo.findFilterSubTypePriceDESC(type, minPrice, maxPrice);
					break;
				}
			}		
			model.addAttribute("listPost", listPost);
			service.checkUser(model, principal);
		}
		catch(Exception e){
			return "electronics";
		}    
	    return "electronics";
	  }
	
	
}
