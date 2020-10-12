package com.gudmarket.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gudmarket.web.entity.Type;

@Repository
public interface TypeRepository extends JpaRepository<Type, String>{

	@Query(value ="select * from type where type_name=?1", nativeQuery = true)
	Type findByTypename(String type_name);
	@Query(value ="select * from type where id_type=?1", nativeQuery = true)
	Type findByTypeId(String id);
	@Query(value ="select * from type where id_cate=?1", nativeQuery = true)
	List <Type> findByCategory(String id_cate);
}
