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

package com.dm.estore.search.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.result.FacetEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Service;

import com.dm.estore.common.converter.ModelToDtoConverter;
import com.dm.estore.common.dto.CategoryDto;
import com.dm.estore.common.dto.CategoryFullDto;
import com.dm.estore.common.dto.ProductDto;
import com.dm.estore.common.dto.ProductSearchDto;
import com.dm.estore.common.dto.ProductSearchResultDto;
import com.dm.estore.search.dto.CatalogItemDto;
import com.dm.estore.search.dto.CatalogItemType;
import com.dm.estore.search.repository.CatalogSearchRepository;
import com.dm.estore.services.search.CatalogSearchService;

/**
 *
 * @author dmorozov
 */
@Service(CatalogSearchService.CONTEXT_NAME)
public class CatalogSearchServiceImpl implements CatalogSearchService {
    
    private final static Logger LOG = LoggerFactory.getLogger(CatalogSearchServiceImpl.class);
	
	@Resource
	private CatalogSearchRepository catalogSearchRepository;
	
	@Resource(name = "catalogItemDtoToCategoryDtoConverter")
    private ModelToDtoConverter<CatalogItemDto, CategoryDto> categoryConverter;
	
	@Resource(name = "catalogItemDtoToProductDtoConverter")
    private ModelToDtoConverter<CatalogItemDto, ProductDto> productConverter;
	
    @Override
    public List<CategoryDto> findHomeCategories() {
        List<CatalogItemDto> catalogItems = catalogSearchRepository.findByActiveTrue(new Sort(Sort.Direction.ASC, CatalogItemDto.FIELD_POPULARITY));
        List<CategoryDto> homeCategories = new ArrayList<CategoryDto>();
        for (CatalogItemDto item : catalogItems) {
            if (CatalogItemType.category.equals(item.getDocumentType())) {
                homeCategories.add(categoryConverter.convert(item));
            }
        }
        
        return homeCategories;
    }

    @Override
    public List<CategoryDto> findCategories(String rootCategory) {
        List<CatalogItemDto> catalogItems = catalogSearchRepository.findByCategoriesAndDocumentType(rootCategory,
                CatalogItemType.category, new Sort(Sort.Direction.ASC, CatalogItemDto.FIELD_POPULARITY));
        
        Integer smalestDepth = Integer.MAX_VALUE;
        Map<Integer, List<CatalogItemDto>> categoriesTree = new HashMap<Integer, List<CatalogItemDto>>();
        
        for (CatalogItemDto item : catalogItems) {
            Integer depth = item.getCategories().size(); 
            if (depth < smalestDepth) {
                smalestDepth = depth;
            }
            
            List<CatalogItemDto> cats = categoriesTree.get(depth);
            if (cats == null) cats = new ArrayList<CatalogItemDto>();
            cats.add(item);
            categoriesTree.put(depth, cats);
        }
        
        return new ArrayList<CategoryDto>(restoreCategoriesTree(categoriesTree, smalestDepth, null));
    }
    
    private List<CategoryFullDto> restoreCategoriesTree(Map<Integer, List<CatalogItemDto>> categoriesTree, Integer currentDepth, List<CategoryFullDto> currentCategories) {
        
        if (!categoriesTree.containsKey(currentDepth)) return null;
        
        List<CatalogItemDto> categories = categoriesTree.get(currentDepth);
        List<CategoryFullDto> nextLevel = new ArrayList<CategoryFullDto>();
        if (currentCategories != null) {
            
            for (CategoryFullDto category : currentCategories) {
                for (CatalogItemDto c : categories) {
                    if (c.getCategories().contains(category.getCode())) {
                        CategoryFullDto categoryDto = (CategoryFullDto) categoryConverter.convert(c, new CategoryFullDto());
                        nextLevel.add(categoryDto);
                        category.getSubcategories().add(categoryDto);
                    }
                }
            }
            
            restoreCategoriesTree(categoriesTree, currentDepth + 1, nextLevel);
            
        } else {
            for (CatalogItemDto item : categories) {
                nextLevel.add((CategoryFullDto) categoryConverter.convert(item, new CategoryFullDto()));
            }
            restoreCategoriesTree(categoriesTree, currentDepth + 1, nextLevel);
            return nextLevel;
        }
        
        return null;
    }

    @Override
    public ProductSearchResultDto findProducts(ProductSearchDto searchRequest) {
        
        Sort sort = new Sort(Sort.Direction.fromString(searchRequest.getSortOrder()), searchRequest.getSortColumn());
        PageRequest pageRequest = new PageRequest(searchRequest.getPage(), searchRequest.getPageSize(), sort);
        FacetPage<CatalogItemDto> catalogItems = catalogSearchRepository.searchProducts(searchRequest.getSearchTerms(), searchRequest.getCategory(), 
                searchRequest.getMaterials(), searchRequest.getColors(),
                pageRequest);

        ProductSearchResultDto result = new ProductSearchResultDto();
        result.setTotalCount(catalogItems.getTotalElements());

        for (Page<? extends FacetEntry> page : catalogItems.getAllFacets()) {
            for (FacetEntry facetEntry : page.getContent()) {
                String key = String.valueOf(facetEntry.getKey());
                String facetCode = facetEntry.getValue();  // name of the category
                long count = facetEntry.getValueCount();      // number of books in this category
                
                if (StringUtils.isEmpty(facetCode)) continue;
                
                if (CatalogItemDto.FIELD_CATEGORY.equalsIgnoreCase(key)) {
                    result.getCategoriesFacet().put(facetCode, count);
                } else if (CatalogItemDto.FIELD_VARIANT_GROUP.equalsIgnoreCase(key)) {
                    result.getGroupFacet().put(facetCode, count);
                } else if (CatalogItemDto.FIELD_CATEGORY.equalsIgnoreCase(key)) {
                    result.getSubGroupFacet().put(facetCode, count);
                }
            }
          }
        
        result.setProducts(productConverter.convert(catalogItems.getContent()));  
        result.setTotalCount((int)(catalogItems.getTotalElements()));
        return result;
    }
}
