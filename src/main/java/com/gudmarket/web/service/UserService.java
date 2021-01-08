package com.gudmarket.web.service;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.gudmarket.web.entity.Account;
import com.gudmarket.web.entity.Token;
import com.gudmarket.web.repository.AccountRepository;
import com.gudmarket.web.repository.TokenRepository;
import com.gudmarket.web.utils.EncrytedPasswordUtils;

@Service
public class UserService {
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private AccountRepository accRepo;
	
	public void saveUser(Account user, String password) {
		setUserId(user);
		user.setPassword(EncrytedPasswordUtils.encrytePassword(password));
		user.setId_level("L01");
		user.setRole(1);
		user.setMoney((double) 0);
		user.setNum_posted(0);
		user.setPost_remain(3);
		accRepo.save(user);
	}
	public void saveGoogle(Account user, String password) {	
		user.setEnabled(true);
		user.setPassword(EncrytedPasswordUtils.encrytePassword(password));
		user.setId_level("L01");
		user.setRole(1);
		user.setMoney((double) 0);
		user.setNum_posted(0);
		user.setPost_remain(3);
		accRepo.save(user);
	}
	public Account getUser( String verificationToken) {
        Token token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }
	
	public Token getVerificationToken( String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }
	
	
	public void createVerificationToken(Account user, String token) {
        Token myToken = new Token(token, user);
        tokenRepository.save(myToken);
    }
	public Token generateNewVerificationToken(String existingVerificationToken) {
        Token vToken = tokenRepository.findByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID().toString());
        vToken = tokenRepository.save(vToken);
        return vToken;
    }
	
	 public String validateVerificationToken(String token) {
	        final Token verificationToken = tokenRepository.findByToken(token);
	        if (verificationToken == null) {
	            return "invalidToken";
	        }

	        final Account user = verificationToken.getUser();
//	        final Calendar cal = Calendar.getInstance();
//	        if ((verificationToken.getExpiryDate()
//	            .getTime() - cal.getTime()
//	            .getTime()) <= 0) {
//	            tokenRepository.delete(verificationToken);
//	            return "expired";
//	        }

	        user.setEnabled(true);
	        accRepo.save(user);
	        return "valid";
	    }
	 public String validateResetPasswordToken(String token) {
	        final Token verificationToken = tokenRepository.findByToken(token);
	        if (verificationToken == null) {
	            return "invalidToken";
	        }
	        return "valid";
	    }
	 
	 public void deleteToken(String token) {
		 Token verificationToken = tokenRepository.findByToken(token);
		 tokenRepository.delete(verificationToken);
	 }
	 public void deleteCode(Account user) {
		 List<Token> verificationToken = tokenRepository.findByListUser(user.getId_user());
		 tokenRepository.deleteAll(verificationToken);
	 }
	 
	 public UserDetails buildUser(Account user) {

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	    authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
	    UserDetails userDetail = new User(user.getId_user(),
		        "", authorities);
		    return userDetail;
        //Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        //SecurityContextHolder.getContext().setAuthentication(authentication);
	 }
	 public boolean isEmail(String emailOrPhone) {
		 for(char c: emailOrPhone.toCharArray()) {
			 if(!Character.isDigit(c)) {
				 return true;
			 }
		 }
		 return false;
	 }
	 public void checkUser(Model model, Principal principal) {
		 User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String userId=loginedUser.getUsername();
				Account user= accRepo.findByUserId(userId);
				model.addAttribute("user", user);
	 }
	 public void setUserId(Account user) {
		 String id="";
			String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	        String[] split=date.split("-");
			do {
				Random rnd = new Random();
				int n = 1000 + rnd.nextInt(9000);
				id="U"+split[0]+split[1]+split[2]+n;
			}
			while(accRepo.findById(id)==null);
			user.setId_user(id);
	 }

}
