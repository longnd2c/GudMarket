package com.gudmarket.web.service;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.gudmarket.web.entity.Account;
import com.gudmarket.web.entity.Post;
import com.gudmarket.web.repository.AccountRepository;
import com.gudmarket.web.repository.PostRepository;
import com.gudmarket.web.repository.TypeRepository;

@Service
public class PostService {
	@Autowired
	private PostRepository postRepo;
	@Autowired
	private UserService service;
	@Autowired
	private TypeRepository typeRepo;
	@Autowired
	private AccountRepository accRepo;
	
	private static String UPLOADED_FOLDER = "E:/HocTap/Eclipse Workspace/GudMarket/src/main/resources/static/webapp/images/posts/";
	
	public void setId(Post post) {
		String id="";
		String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String[] split=date.split("-");
		do {
			Random rnd = new Random();
			int n = 1000 + rnd.nextInt(9000);
			id=split[0]+split[1]+split[2]+"_"+n;
		}
		while(postRepo.findById(id)==null);
		post.setId_post(id);
	}
	
	public void savePost(Post post, Principal principal, MultipartFile[] uploadingFiles, ModelAndView modelAndView) {
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		  String userId=loginedUser.getUsername();
		  
		String[] listType= post.getType().getId_type().split(",");
		  String typeId="";
		  for(String t :listType) {
			  if(!t.equals("0")) {
				 typeId=t;
			  }
		  }
		  post.setType(typeRepo.findByTypeId(typeId));
		  
		  try {
			  setId(post);
			  int img_num=1;
			  for(MultipartFile uploadedFile : uploadingFiles) {
				  //System.out.println(uploadedFile.getOriginalFilename());
				  if(!uploadedFile.getOriginalFilename().equals("")) {
					  String fileName=post.getId_post()+"-img0"+img_num+".jpg";
					  File file = new File(UPLOADED_FOLDER + fileName);
			            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
	                    stream.write(uploadedFile.getBytes());
	                    stream.close();
	                    img_num=img_num+1;
			            //uploadedFile.transferTo(file);
				  }
		        }
			  MultipartFile upFile=uploadingFiles[0];
			  if(upFile.getOriginalFilename().equals("")){
				  post.setImg(null);
			  }
			  else {
				  String imgString="";
			      String URL="/webapp/images/posts/";
			      for(int i=1; i<img_num; i++) {
			    	  imgString=imgString+URL+post.getId_post()+"-img0"+i+".jpg"+"@";
			      }
		    	  post.setImg(imgString);
			  }
			  
			  post.setUser(accRepo.findByUserId(userId));
				post.setStatus(false);
				postRepo.save(post);
		      
		      //-1 POST REMAIN AND +1 POSTED///
		      
					Account acc=accRepo.findByUserId(userId);
					acc.setNum_posted(acc.getNum_posted()+1);
					acc.setPost_remain(acc.getPost_remain()-1);
					accRepo.save(acc);
				
		    } catch (Exception e) {
		      e.printStackTrace();
		      modelAndView.addObject("message","Upload failed");
		    }
		  modelAndView.setViewName("redirect:/index"); 
	}
}
