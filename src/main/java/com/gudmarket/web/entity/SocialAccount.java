package com.gudmarket.web.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "socialaccount")
public class SocialAccount {
	@Id
	  @Column(name = "id")
	  private String id;
	  
	  @Column(name = "email")
	  private String email;
	  
	  @Column(name = "full_name")
	  private String full_name;
	  
	  @Column(name = "phone")
	  private String phone;
	  
	  @Column(name = "address")
	  private String address;
	  
	  @Column(name = "money")
	  private Double money;
	  
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
	  
	  

	public SocialAccount() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getId_level() {
		return id_level;
	}

	public void setId_level(String id_level) {
		this.id_level = id_level;
	}

	public Integer getNum_posted() {
		return num_posted;
	}

	public void setNum_posted(Integer num_posted) {
		this.num_posted = num_posted;
	}

	public Integer getPost_remain() {
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

	public String toString()
	{
		String str=this.id+this.email+this.full_name+this.phone+this.address+this.num_posted+this.id_level+this.post_remain+this.money+this.reg_date+this.block_to;
		return str.toLowerCase();
	}
	  
	  
	 
}
