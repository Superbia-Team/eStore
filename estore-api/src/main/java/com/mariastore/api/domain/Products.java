package com.mariastore.api.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.mariastore.core.domain.Product;

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
