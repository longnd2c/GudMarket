package com.gudmarket.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.gudmarket.web.entity.Account;
import com.gudmarket.web.repository.AccountRepository;
import com.gudmarket.web.service.SellerService;

@Controller
public class AdminController {
	@Autowired
	AccountRepository accRepo;
	@Autowired
	SellerService sellerService;
	
	@RequestMapping("/seller-list")
	  public String listAdmin(Model model) {
	    model.addAttribute("listSeller", accRepo.findSeller());
	    return "seller-list";
	  }
	
	@RequestMapping("/seller-add")
	  public String addSeller(Model model) {
	    model.addAttribute("seller", new Account());
	    return "seller-add";
	  }
	
	@RequestMapping("/seller-update/{username}")
	  public String updateTrainee(@PathVariable String username, Model model) {
		Optional<Account> seller = accRepo.findById(username);
		if (seller.isPresent()) {
	        model.addAttribute("seller", seller.get());
	     }
	    return "seller-update";
	  }
	@RequestMapping("/saveSeller") 
	  public ModelAndView doSaveSeller(@ModelAttribute("seller") Account seller, BindingResult bindingResult, 
			  			@RequestParam(value = "username") String username, @RequestParam(value ="password") String password) { 
		  ModelAndView modelAndView=new ModelAndView();
		  Account accExists = accRepo.findByUsername(username);
	        if (accExists != null) {
	        	bindingResult.rejectValue("username", "error.username","There is already a user registered with the Username provided");
	        }
	        if (bindingResult.hasErrors()) {
	            modelAndView.setViewName("seller-add");
	        }
	        else {
		  sellerService.saveSeller(seller, password);
		  modelAndView.addObject("listSeller",accRepo.findSeller());
		  modelAndView.setViewName("seller-list"); 
	        }
	  	
	  	return modelAndView; 
	  }
	
	@RequestMapping("/updateSeller")
	  public ModelAndView doUpdateSeller(@ModelAttribute("seller") Account seller, BindingResult bindingResult) {  
		  ModelAndView modelAndView=new ModelAndView();
		  System.out.println(seller.toString());
		  accRepo.save(seller);
		  modelAndView.addObject("listSeller",accRepo.findSeller());
		  modelAndView.setViewName("seller-list"); 
	        return modelAndView;
	  }
	  
	  @RequestMapping("/sellerDelete/{username}")
	  public String doDeleteTrainee(@PathVariable String username, Model model) {
		  accRepo.deleteById(username);
		  
	    model.addAttribute("listSeller",accRepo.findSeller());
	    return "seller-list";
	  }
	  
	  @RequestMapping("/searchSeller")
	  public String search(@RequestParam(value = "searchKey") String searchKey, Model model) {
		List<Account> listSeller = new ArrayList<Account>();
		for(Account seller:accRepo.findSeller()) {
			if(seller.toString().contains(searchKey)) {
				listSeller.add(seller);
			}
		}
	    model.addAttribute("listSeller", listSeller);
	    return "seller-list";
	  }
}
