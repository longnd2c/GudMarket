package com.gudmarket.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gudmarket.web.entity.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, String>{
	@Query(value ="select * from Account a where a.username=?1", nativeQuery = true)
	Account findByUsername(String username);
	@Query(value ="select * from Account a where a.role=1", nativeQuery = true)
	Iterable <Account> findSeller();
}
