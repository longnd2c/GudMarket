package com.gudmarket.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.gudmarket.web.entity.Account;
import com.gudmarket.web.entity.Post;
import com.gudmarket.web.entity.Type;
import com.gudmarket.web.repository.AccountRepository;
import com.gudmarket.web.repository.PostRepository;
import com.gudmarket.web.repository.TypeRepository;
import com.gudmarket.web.service.SellerService;
import com.gudmarket.web.service.TypeService;

@Controller
public class AdminController {
	@Autowired
	AccountRepository accRepo;
	@Autowired
	SellerService sellerService;
	@Autowired
	PostRepository postRepo;
	@Autowired
	TypeRepository typeRepo;
	@Autowired
	TypeService typeService;
		
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date - dd/MM/yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	///////////////////////SELLER///////////////////////////////////////////
	@RequestMapping("/seller-list")
	  public String listseller(Model model) {
		Iterable<Account> list=accRepo.findSeller();
		Date now = new Date();
		Date block=null;
		for(Account acc : list) {
			block=acc.getBlock_to();
			if(block!=null && block.before(now)) {
				acc.setBlock_to(null);
				accRepo.save(acc);
			}
		}
		model.addAttribute("num_post", postRepo.findByStatus().size());
	    model.addAttribute("listSeller", list);
	    return "seller-list";
	  }
	
	
	@RequestMapping("/sellerBlock")
	  public String blockseller(@RequestParam(value = "userId") String userId,@RequestParam(value = "blockTime") String blockTime ,Model model) {
		Account acc=accRepo.findByUserId(userId);
		int date=Integer.parseInt(blockTime);
		if(date==0) {
			model.addAttribute("num_post", postRepo.findByStatus().size());
			model.addAttribute("listSeller", accRepo.findSeller());
		    return "seller-list";
		}
		Date dt = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(dt); 
		c.add(Calendar.DATE, date);
		dt = c.getTime();
		acc.setBlock_to(dt);
		accRepo.save(acc);
		model.addAttribute("num_post", postRepo.findByStatus().size());
	    model.addAttribute("listSeller", accRepo.findSeller());
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
		model.addAttribute("num_post", postRepo.findByStatus().size());
	    model.addAttribute("listSeller", listSeller);
	    return "seller-list";
	  }
	  ///////////////////////////////////POST///////////////////////////////////////////
	  @RequestMapping("/post-list")
	  public String listPost(Model model) {
		  model.addAttribute("num_post", postRepo.findByStatus().size());
	    model.addAttribute("listPost", postRepo.arrangeAllByStatus());
	    return "post-list";
	  }
	@RequestMapping("/postDelete/{id}")
	  public String doDeletePost(@PathVariable String id, Model model) {
		  postRepo.deleteById(id);
		  
		  model.addAttribute("num_post", postRepo.findByStatus().size());
	    model.addAttribute("listPost",postRepo.arrangeAllByStatus());
	    return "post-list";
	  }
	  
	  @RequestMapping("/searchPost")
	  public String searchPost(@RequestParam(value = "searchKey") String searchKey, Model model) {
		List<Post> listPost = new ArrayList<Post>();
		for(Post post:postRepo.findAll()) {
			if(post.toString().contains(searchKey)) {
				listPost.add(post);
			}
		}
		model.addAttribute("num_post", postRepo.findByStatus().size());
	    model.addAttribute("listPost", listPost);
	    return "post-list";
	  }
	  @RequestMapping("/post-update/{id}")
	  public String updatePost(@PathVariable String id, Model model) {
		Optional<Post> post = postRepo.findById(id);
		if (post.isPresent()) {
			Post temp=post.get();
			temp.setStatus(true);
			postRepo.save(temp);
			model.addAttribute("num_post", postRepo.findByStatus().size());
	        model.addAttribute("listPost", postRepo.arrangeAllByStatus());
	     }
	    return "post-list";
	  }
	  //////////////////////////////////TYPE////////////////////////////////
	  @RequestMapping("/type-list")
	  public String listType(Model model) {
	    model.addAttribute("listType", typeRepo.findAll());
	    model.addAttribute("num_post", postRepo.findByStatus().size());
	    return "type-list";
	  }
	@RequestMapping("/type-add")
	  public String addType(Model model) {
	    model.addAttribute("type", new Type());
	    return "type-add";
	  }
	@RequestMapping("/saveType") 
	  public ModelAndView doSaveType(@ModelAttribute("type") Type type, BindingResult bindingResult, 
			  @RequestParam(value = "type_name") String type_name) { 
		  ModelAndView modelAndView=new ModelAndView();
		  Type typeExists = typeRepo.findByTypename(type_name);
	        if (typeExists != null) {
	        	bindingResult.rejectValue("type_name", "error.type_name","There is already a type created with the Name provided");
	        }
	        if (bindingResult.hasErrors()) {
	            modelAndView.setViewName("type-add");
	        }
	        else {
		  typeService.saveType(type,type_name);
		  modelAndView.addObject("num_post", postRepo.findByStatus().size());
		  modelAndView.addObject("listType",typeRepo.findAll());
		  modelAndView.setViewName("type-list"); 
	        }
	  	return modelAndView; 
	  }
	@RequestMapping("/typeDelete/{id}")
	  public String doDeleteType(@PathVariable String id, Model model) {
		  typeRepo.deleteById(id);
		  model.addAttribute("num_post", postRepo.findByStatus().size());
	    model.addAttribute("listType", typeRepo.findAll());
	    return "type-list";
	  }
	  
	  @RequestMapping("/searchType")
	  public String searchType(@RequestParam(value = "searchKey") String searchKey, Model model) {
		List<Type> listType = new ArrayList<Type>();
		for(Type type:typeRepo.findAll()) {
			if(type.toString().contains(searchKey)) {
				listType.add(type);
			}
		}
		model.addAttribute("num_post", postRepo.findByStatus().size());
	    model.addAttribute("listType", listType);
	    return "type-list";
	  }
}
