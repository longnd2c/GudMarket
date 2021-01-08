package com.gudmarket.web.google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gudmarket.web.entity.Account;
import com.gudmarket.web.repository.AccountRepository;


@Component
public class GoogleUtils {
	 @Autowired
	  private Environment env;
	 @Autowired
	  private AccountRepository accRepo;
	 
	  public String getToken(final String code) throws ClientProtocolException, IOException {
	    String link = env.getProperty("google.link.get.token");
	    String response = Request.Post(link)
	        .bodyForm(Form.form().add("client_id", env.getProperty("google.app.id"))
	            .add("client_secret", env.getProperty("google.app.secret"))
	            .add("redirect_uri", env.getProperty("google.redirect.uri")).add("code", code)
	            .add("grant_type", "authorization_code").build())
	        .execute().returnContent().asString();
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode node = mapper.readTree(response).get("access_token");
	    return node.textValue();
	  }
	  
	  public GooglePojo getUserInfo(final String accessToken) throws ClientProtocolException, IOException {
	    String link = env.getProperty("google.link.get.user_info") + accessToken;
	    String response = Request.Get(link).execute().returnContent().asString();
	    ObjectMapper mapper = new ObjectMapper();
	    GooglePojo googlePojo = mapper.readValue(response, GooglePojo.class);
	    System.out.println(googlePojo.toString());
	    return googlePojo;
	  }
	  public Account findAccount(GooglePojo googlePojo) {
		  Account accExist= accRepo.findByEmailOrPhone(googlePojo.getEmail());
		  if(accExist==null) {
			  return null;
		  }
		  else{
			  if(!accExist.isEnabled()) {
				  accExist.setEnabled(true);
			  }
			  return accExist;
		  }
	  }
	  public Account saveAccount(GooglePojo googlePojo) {
			  Account acc =new Account();
			  acc.setId_user(googlePojo.getId());
			  acc.setEmail(googlePojo.getEmail());
			  acc.setFull_name(googlePojo.getName());
			  return acc;
	  }
	  
	  public UserDetails buildUser(Account acc) {
	    boolean enabled = true;
	    boolean accountNonExpired = true;
	    boolean credentialsNonExpired = true;
	    boolean accountNonLocked = true;
	    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	    authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
	    UserDetails userDetail = new User(acc.getId_user(),
	        "", enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	    return userDetail;
	  }

}
