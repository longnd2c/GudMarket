package com.gudmarket.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gudmarket.web.entity.Account;
import com.gudmarket.web.entity.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
	@Query(value ="select * from Token v where v.token=?1", nativeQuery = true)
	Token findByToken(String token);
	@Query(value ="select * from Token where id_user=?1", nativeQuery = true)
	Token findByUser(String id);
	@Query(value ="select * from Token where id_user=?1", nativeQuery = true)
	List<Token> findByListUser(String id);
}
