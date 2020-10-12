package com.gudmarket.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gudmarket.web.entity.Category;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, String>{
	
}
