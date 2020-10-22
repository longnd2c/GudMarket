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
	
	@Query(value ="select * from post where priority IS NOT NULL", nativeQuery = true)
	List<Post> findPriority();
	
	@Query(value ="SELECT * FROM post p  WHERE status=true AND p.price BETWEEN ?1 and ?2 ORDER BY priority desc, date desc", nativeQuery = true)
	List<Post> findFilter(String minPrice, String maxPrice );
	
	@Query(value ="SELECT * FROM post p  WHERE status=true AND p.price BETWEEN ?1 and ?2 ORDER BY priority desc, price desc", nativeQuery = true)
	List<Post> findFilterPriceDESC(String minPrice, String maxPrice );
	
	@Query(value ="SELECT * FROM post p  WHERE status=true AND p.price BETWEEN ?1 and ?2 ORDER BY priority desc, price asc", nativeQuery = true)
	List<Post> findFilterPriceASC(String minPrice, String maxPrice );
	
	@Query(value ="SELECT * FROM post p LEFT JOIN type t ON p.id_type=t.id_type WHERE status=true AND t.id_cate=?1 AND p.price BETWEEN ?2 and ?3 ORDER BY priority desc, date desc", nativeQuery = true)
	List<Post> findFilterType(String type, String minPrice, String maxPrice );
	
	@Query(value ="SELECT * FROM post p LEFT JOIN type t ON p.id_type=t.id_type WHERE status=true AND t.id_cate=?1 AND p.price BETWEEN ?2 and ?3 ORDER BY priority desc, price desc", nativeQuery = true)
	List<Post> findFilterTypePriceDESC(String type, String minPrice, String maxPrice );
	
	@Query(value ="SELECT * FROM post p LEFT JOIN type t ON p.id_type=t.id_type WHERE status=true AND t.id_cate=?1 AND p.price BETWEEN ?2 and ?3 ORDER BY priority desc, price asc", nativeQuery = true)
	List<Post> findFilterTypePriceASC(String type, String minPrice, String maxPrice );

}
