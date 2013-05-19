package com.mariastore.core.domain;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Product {

	private long id;
	
	private String sku;

	private String name;

	@XmlElement(name="i")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@XmlElement(name="s")
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	@XmlElement(name="n")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
