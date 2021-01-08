package com.gudmarket.web.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gudmarket.web.entity.Account;
import com.gudmarket.web.repository.AccountRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
 
    @Autowired
    private AccountRepository accRepo;
    @Autowired
    private UserService service;
 
 
    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        Account account= this.accRepo.findByUserStatus(emailOrPhone);
        
        if (account == null) {
            System.out.println("User not found! " + emailOrPhone);
            throw new UsernameNotFoundException("User " + emailOrPhone + " was not found in the database");
        }
 
        System.out.println("Found User: " + emailOrPhone);
 
        int role = account.getRole();
        String roleName=null;
        if(role==0) {
        	roleName="ROLE_ADMIN";
        }
        else {
        	roleName="ROLE_SELLER";
        }
        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();

        if (roleName != null) {
        	GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
            grantList.add(authority);
        }
        UserDetails userDetails = (UserDetails) new User(account.getId_user(), //
                account.getPassword(), grantList);
        
        return userDetails;
    }
 
}
