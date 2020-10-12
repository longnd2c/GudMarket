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
 
 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account= this.accRepo.findByUserStatus(username);
        
        if (account == null) {
            System.out.println("User not found! " + username);
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        }
 
        System.out.println("Found User: " + username);
 
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
 
        UserDetails userDetails = (UserDetails) new User(account.getUsername(), //
                account.getPassword(), grantList);

        return userDetails;
    }
 
}
