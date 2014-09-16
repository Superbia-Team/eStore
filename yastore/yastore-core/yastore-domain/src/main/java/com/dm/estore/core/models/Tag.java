package com.dm.estore.core.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TAGS")
public class Tag extends AbstractEntity {

	private static final long serialVersionUID = -8753623463464391680L;

	@Column(name = "CODE")
	private String code;
	
	public Tag() {}
	public Tag(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
