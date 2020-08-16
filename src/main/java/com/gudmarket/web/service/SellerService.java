package com.gudmarket.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gudmarket.web.utils.EncrytedPasswordUtils;
import com.gudmarket.web.entity.Account;
import com.gudmarket.web.repository.AccountRepository;

@Service
public class SellerService {
	
	private AccountRepository accRepo;
	@Autowired
	public SellerService(AccountRepository accRepo) {
		this.accRepo=accRepo;
	}
	
	public void saveSeller(Account seller, String password) {
		seller.setPassword(EncrytedPasswordUtils.encrytePassword(password));
		seller.setId_level("L01");
		seller.setRole(1);
		seller.setMoney(0);
		seller.setNum_posted(0);
		seller.setPost_remain(3);
		accRepo.save(seller);
	}
}
