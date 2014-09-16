/*
 * Copyright 2014 Denis Morozov.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dm.estore.core.repository;

import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.dm.estore.core.models.I18NStringValue;
import com.dm.estore.core.models.Product;

/**
 * Repository to access {@link Product} instances.
 * 
 * @author dmorozov
 */
@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

	/**
	 * Find product by SKU code
	 * 
	 * @param sku SKU code
	 * @return
	 */
	Product findBySku(String sku);
	
	/**
	 * Find localized name for the product
	 * 
	 * @param id product's id
	 * @param locale locale
	 * @return
	 */
	@Query("select VALUE(n) from Product p join p.name.strings n where p.id = ?1 and KEY(n) = ?2")
	I18NStringValue findName(Long id, Locale locale);
	
	/**
	 * Returns all {@link Product}s having the given attribute.
	 * 
	 * @param attribute
	 * @return
	 */
	@Query("select p from Product p where p.attributes[?1] = ?2")
	List<Product> findByAttributeAndValue(String attribute, String value);
}
