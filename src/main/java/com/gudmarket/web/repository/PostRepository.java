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
	
	@Query(value ="select * from post where id_post=?1", nativeQuery = true)
	Post findByPostId(String id);
	
	@Query(value ="select * from post order by status asc", nativeQuery = true)
	List<Post> arrangeAllByStatus();
	
	@Query(value ="select * from post where status = false", nativeQuery = true)
	List<Post> findByStatus();
	
	@Query(value ="select * from post where status=true order by priority desc, date desc", nativeQuery = true)
	List<Post> findAllByPriorityDate();
	
	@Query(value ="select * from post where username=?1 order by date desc limit 3", nativeQuery = true)
	List<Post> findSellerNewPost(String username);
	
	@Query(value ="select * from post where username=?1 order by date desc", nativeQuery = true)
	List<Post> findSellerAllPost(String username);

}
