package com.mariastore.core.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "products")
public class Products extends RestCollection<Product> {
	
	public Products(){ super(); }
	public Products(List<Product> products) {
		super(products);
	}
	
	@XmlElement(name = "product")
	public List<Product> getList() {
		return super.getList();
	}
}
