package com.gudmarket.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gudmarket.web.entity.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, String>{
	@Query(value ="select * from Account a where a.id_user=?1", nativeQuery = true)
	Account findByUserId(String userId);
	@Query(value ="select * from Account a where a.email=?1 or a.phone=?1", nativeQuery = true)
	Account findByEmailOrPhone(String emailOrPhone);
	@Query(value ="select * from Account a where a.role=1", nativeQuery = true)
	Iterable <Account> findSeller();
	@Query(value ="select * from Account a where a.email=?1 or a.phone=?1 and a.enabled=1", nativeQuery = true)
	Account findByUserStatus(String emailOrPhone);
}
