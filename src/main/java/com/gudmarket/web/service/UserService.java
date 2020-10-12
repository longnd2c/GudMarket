package com.gudmarket.web.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import com.gudmarket.web.entity.SocialAccount;
import com.gudmarket.web.entity.Token;
import com.gudmarket.web.repository.AccountRepository;
import com.gudmarket.web.repository.SocialAccountRepository;
import com.gudmarket.web.repository.TokenRepository;
import com.gudmarket.web.utils.EncrytedPasswordUtils;

@Service
public class UserService {
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private AccountRepository accRepo;
	@Autowired
	private SocialAccountRepository socialAccRepo;
	
	public void saveUser(Account user, String password) {
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
	
	public void deleteUser( Account user) {
        Token verificationToken = tokenRepository.findByUser(user);

        if (verificationToken != null) {
            tokenRepository.delete(verificationToken);
        }

        //PasswordResetToken passwordToken = passwordTokenRepository.findByUser(user);

        //if (passwordToken != null) {
        //    passwordTokenRepository.delete(passwordToken);
        //}

        accRepo.delete(user);
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
	 
	 public UserDetails buildUser(Account user) {

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	    authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
	    UserDetails userDetail = new User(user.getUsername(),
		        "", authorities);
		    return userDetail;
        //Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        //SecurityContextHolder.getContext().setAuthentication(authentication);
    }
	 
	 public void checkUser(Model model, Principal principal) {
		 User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String username=loginedUser.getUsername();
			if(username.contains("@")) {
				SocialAccount socialAcc=socialAccRepo.findByEmail(username);
				model.addAttribute("user", socialAcc);
			}
			else {
				Account user= accRepo.findByUsername(username);
				model.addAttribute("user", user);
			}
	 }

}
