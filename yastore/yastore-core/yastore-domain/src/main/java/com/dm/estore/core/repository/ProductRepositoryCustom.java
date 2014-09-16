package com.dm.estore.core.repository;

import java.util.List;

import com.dm.estore.core.models.Product;

public interface ProductRepositoryCustom {
	
	List<Product> findProductWithGreenColor(final String color);

}
