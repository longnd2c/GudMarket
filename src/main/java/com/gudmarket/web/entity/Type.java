package com.gudmarket.web.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "type")
public class Type {
	@Id
	  @Column(name = "id_type")
	  private String id_type;
	
	  @Column(name = "type_name")
	  private String type_name;

	public String getId_type() {
		return id_type;
	}

	public void setId_type(String id_type) {
		this.id_type = id_type;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	
	public String toString() {
		String str = this.id_type + this.type_name;
		return str.toLowerCase();
	}
}
