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

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.dm.estore.core.models.Category;

/**
 * Repository to access {@link Category} instances.
 * 
 * @author dmorozov
 */
@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
public interface CategoryRepository extends JpaRepository<Category, Long> {

	/**
	 * Returns all {@link Category}s having the given attribute.
	 * 
	 * @param attribute
	 * @return
	 */
	@Query("select c from Category c where c.attributes[?1] = ?2")
	List<Category> findByAttributeAndValue(String attribute, String value);
	
	List<Category> findByHomeCategoryTrue(Sort sort);
}
