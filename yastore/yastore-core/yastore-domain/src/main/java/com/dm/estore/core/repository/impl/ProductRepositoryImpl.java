package com.dm.estore.core.repository.impl;

import java.util.List;

import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;

import com.dm.estore.core.models.Product;
import com.dm.estore.core.models.QProduct;
import com.dm.estore.core.models.QTag;
import com.dm.estore.core.repository.ProductRepositoryCustom;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.types.expr.BooleanExpression;

public class ProductRepositoryImpl extends QueryDslRepositorySupport implements ProductRepositoryCustom {

	public ProductRepositoryImpl() {
		super(Product.class);
	}

	@Override
	public List<Product> findProductWithGreenColor(String color) {

		QProduct product = QProduct.product;
		QTag attribute = QTag.tag;
		BooleanExpression q = new JPASubQuery().from(attribute)
		    .where(
		    	attribute.in(product.tags), 
		    	attribute.code.toLowerCase().eq("red")
		    ).exists();
		
		return from(product).where(q).list(product);
	}
}
