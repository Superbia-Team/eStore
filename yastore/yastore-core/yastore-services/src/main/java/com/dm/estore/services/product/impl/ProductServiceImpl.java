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
package com.dm.estore.services.product.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dm.estore.common.converter.ModelToDtoConverter;
import com.dm.estore.common.exceptions.ConvertionException;
import com.dm.estore.common.dto.CategoryDto;
import com.dm.estore.core.models.Category;
import com.dm.estore.core.repository.CategoryRepository;
import com.dm.estore.services.product.ProductService;
import com.dm.estore.services.search.CatalogSearchService;

@Service(ProductService.CONTEXT_NAME)
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    @Resource(name = CatalogSearchService.CONTEXT_NAME)
    private CatalogSearchService searchService;
    
    @Resource(name = "categoryModelToDtoConverter")
    private ModelToDtoConverter<Category, CategoryDto> categoryConverter;
    
    @Resource
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findHomeCategories() {
        List<CategoryDto> homeCategories = null;
        try {
            List<Category> categories = categoryRepository.findByHomeCategoryTrue(new Sort(Sort.Direction.ASC, "displayOrder"));
            if (!CollectionUtils.isEmpty(categories)) {
                homeCategories = categoryConverter.convert(categories);
            }
        } catch (ConvertionException e) {
            LOG.error("Unable to process DAO result set", e);
            throw e;
        }
        
        return homeCategories != null ? homeCategories : (homeCategories = Collections.emptyList());
    }

}
