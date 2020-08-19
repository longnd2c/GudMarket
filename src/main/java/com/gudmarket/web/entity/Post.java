package com.gudmarket.web.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	  
	  @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "username", nullable = false)
	    private Account acc;
	  
	  @Column(name = "id_type")
	  private String id_type;
	  
	  @Column(name = "description")
	  private String description;
	  
	  @Column(name = "img")
	  private String img;
	  
	  @Column(name = "price")
	  private int price;
	  
	  @Column(name = "contact")
	  private String contact;
	  
	  @CreationTimestamp
	  @Temporal(TemporalType.TIMESTAMP)
	  @Column(name = "date")
	  private Date date;
	  
	  @Column(name = "status")
	  private int status;

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

	public Account getAcc() {
		return acc;
	}

	public void setAcc(Account acc) {
		this.acc = acc;
	}

	public String getId_type() {
		return id_type;
	}

	public void setId_type(String id_type) {
		this.id_type = id_type;
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String toString() {
		String str=this.id_post+this.title+this.contact+this.description+this.id_type+this.img+this.price+this.status+this.acc+this.date;
		return str.toLowerCase();
		}
	
}
