package com.gudmarket.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.gudmarket.web.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
	@Query(value ="select * from post where username=?1", nativeQuery = true)
	Post findByUsername(String username);
	@Query(value ="select * from post order by status asc", nativeQuery = true)
	List<Post> arrangeAllByStatus();

}
