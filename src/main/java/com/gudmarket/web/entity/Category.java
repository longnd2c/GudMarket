package com.gudmarket.web.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "category")
public class Category {
	@Id
	  @Column(name = "id_cate")
	  private String id_cate;
	
	@Column(name = "cate_name")
	  private String cate_name;

	public String getId_cate() {
		return id_cate;
	}

	public void setId_cate(String id_cate) {
		this.id_cate = id_cate;
	}

	public String getCate_name() {
		return cate_name;
	}

	public void setCate_name(String cate_name) {
		this.cate_name = cate_name;
	}
	
}
