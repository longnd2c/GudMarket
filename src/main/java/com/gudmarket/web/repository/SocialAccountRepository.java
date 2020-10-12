package com.gudmarket.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.gudmarket.web.entity.SocialAccount;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, String>{
	@Query(value ="select * from SocialAccount a where a.email=?1", nativeQuery = true)
	SocialAccount findByEmail(String email);
}
