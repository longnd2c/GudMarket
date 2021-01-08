package com.gudmarket.web.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "post")
public class Post {
	@Id
	  @Column(name = "id_post")
	  private String id_post;
	  
	  @Column(name = "title")
	  private String title;
	  
	  //@ManyToOne(fetch = FetchType.LAZY)
	    //@JoinColumn(name = "username", nullable = false)
	    //private Account acc;
	  @ManyToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = "id_user", nullable = false)
	  private Account user;
	  
	  @ManyToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = "id_type", nullable = false)
	  private Type type;
	  
	  @Column(name = "description")
	  private String description;
	  
	  @Column(name = "img")
	  private String img;
	  
	  @Column(name = "price")
	  private Double price;
	  
	  @Column(name = "contact")
	  private String contact;
	  
	  @CreationTimestamp
	  @Temporal(TemporalType.TIMESTAMP)
	  @Column(name = "date")
	  private Date date;
	  
	  @Column(name = "priority")
	  private Date priority;
	  
	  @Column(name = "status")
	  private boolean status;

	public String getId_post() {
		return id_post;
	}

	public void setId_post(String id_post) {
		this.id_post = id_post;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	

	

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public Date getPriority() {
		return priority;
	}

	public void setPriority(Date priority) {
		this.priority = priority;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String toString() {
		String str=this.id_post+this.title+this.contact+this.description+this.getType().toString()+this.img+this.price+this.status+this.user.getId_user()+this.date;
		return str.toLowerCase();
		}
	
}
