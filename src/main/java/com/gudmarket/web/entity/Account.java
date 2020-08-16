package com.gudmarket.web.entity;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "account")
public class Account {
	
	@Id
	  @Column(name = "username")
	  private String username;
	  
	  @Column(name = "password")
	  private String password;
	  
	  @Column(name = "role")
	  private Integer role;
	  
	  @Column(name = "full_name")
	  private String full_name;
	  
	  @Column(name = "email")
	  private String email;
	  
	  @Column(name = "phone")
	  private String phone;
	  
	  @Column(name = "money")
	  private Integer money;
	  
	  @Column(name = "id_level")
	  private String id_level;
	  
	  @Column(name = "num_posted")
	  private Integer num_posted;
	  
	  @Column(name = "post_remain")
	  private Integer post_remain;
	  
	  @CreationTimestamp
	  @Temporal(TemporalType.TIMESTAMP)
	  @Column(name = "reg_date")
	  private Date reg_date;
	  
	  @Column(name = "block_to")
	  private Date block_to;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(Integer money) {
		this.money = money;
	}

	public String getId_level() {
		return id_level;
	}

	public void setId_level(String id_level) {
		this.id_level = id_level;
	}

	public int getNum_posted() {
		return num_posted;
	}

	public void setNum_posted(Integer num_posted) {
		this.num_posted = num_posted;
	}

	public int getPost_remain() {
		return post_remain;
	}

	public void setPost_remain(Integer post_remain) {
		this.post_remain = post_remain;
	}

	public Date getReg_date() {
		return reg_date;
	}

	public void setReg_date(Date reg_date) {
		this.reg_date = reg_date;
	}

	public Date getBlock_to() {
		return block_to;
	}

	public void setBlock_to(Date block_to) {
		this.block_to = block_to;
	}

	public String toString() {
		String str=this.username+this.full_name+this.id_level+this.email+this.phone+this.money+this.num_posted+this.post_remain+this.reg_date+this.block_to;
		return str.toLowerCase();
	}
	  
	  
}
