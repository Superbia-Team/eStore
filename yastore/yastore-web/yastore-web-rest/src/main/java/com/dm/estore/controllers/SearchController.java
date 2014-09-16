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
package com.dm.estore.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import com.dm.estore.common.ExecutionCallback;
import com.dm.estore.common.akka.ServicesConstants;
import com.dm.estore.common.dto.ProductSearchDto;
import com.dm.estore.common.dto.requests.HomeCategoriesRequest;
import com.dm.estore.common.dto.requests.ProductsSearchRequest;
import com.dm.estore.common.dto.requests.SearchCategoriesRequest;
import com.dm.estore.common.utils.ActorUtils;
import com.dm.estore.controllers.common.QueryConstants;
import com.dm.estore.common.dto.CategoryDto;
import com.dm.estore.common.dto.ProductDto;
import com.dm.estore.common.dto.ProductSearchResultDto;

@Controller
@RequestMapping(QueryConstants.API_SEARCH)
public class SearchController {
    
    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);
    
    private static final String ABOUT_CATEGORY = "ABOUT";
    private static final String MSG_SPEC_CAT_NAME = "lable.category.special.about.name";
    private static final String MSG_SPEC_CAT_SUMMARY = "lable.category.special.about.summary";
    private static final String MSG_SPEC_CAT_IMG = "lable.category.special.about.image";
    private static final String MSG_SPEC_CAT_LNK = "#about";
    
    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value="homeCategories", method=RequestMethod.GET)
    @ResponseBody
    public DeferredResult<List<CategoryDto>> homeCategories(final Locale locale) {
        LOG.debug("Query home categories - START. Thread: [{}]", Thread.currentThread().getName());
        final DeferredResult<List<CategoryDto>> deferredResult = new DeferredResult<List<CategoryDto>>(null, Collections.emptyList());

        ActorUtils.callAsyncService(ServicesConstants.SERVICE_PRODUCT, new HomeCategoriesRequest(), new ExecutionCallback<List<CategoryDto>>() {
            @Override
            public void done(List<CategoryDto> result) {
                LOG.debug("Get Home Categories - DONE. Thread: [{}]", Thread.currentThread().getName());
                
                List<CategoryDto> categories = new ArrayList<CategoryDto>(result);
                CategoryDto aboutCategory = new CategoryDto();
                aboutCategory.setCode(ABOUT_CATEGORY);
                aboutCategory.setName(messageSource.getMessage(MSG_SPEC_CAT_NAME, null, locale));
                aboutCategory.setSummary(messageSource.getMessage(MSG_SPEC_CAT_SUMMARY, null, locale));
                aboutCategory.setImage(messageSource.getMessage(MSG_SPEC_CAT_IMG, null, locale));
                aboutCategory.setLink(MSG_SPEC_CAT_LNK);
                categories.add(aboutCategory);
                
                deferredResult.setResult(categories);
            }
            @Override
            public void failed(Throwable e) {
                LOG.error("Unable to query home categories", e);
                List<CategoryDto> categories = Collections.emptyList(); 
                deferredResult.setResult(categories);
                deferredResult.setErrorResult(e);
            }
        });

        LOG.debug("Query home categories - EXIST. Thread: [{}]", Thread.currentThread().getName());
        return deferredResult;
    }
    
    @RequestMapping(value="categories", method=RequestMethod.GET)
    @ResponseBody
    public DeferredResult<List<CategoryDto>> categories(@RequestParam(value="parentCategory") String parentCategory) {
        LOG.debug("Query home categories - START. Thread: [{}]", Thread.currentThread().getName());
        final DeferredResult<List<CategoryDto>> deferredResult = new DeferredResult<List<CategoryDto>>(null, Collections.emptyList());

        ActorUtils.callAsyncService(ServicesConstants.SERVICE_PRODUCT, new SearchCategoriesRequest(parentCategory), new ExecutionCallback<List<CategoryDto>>() {
            @Override
            public void done(List<CategoryDto> result) {
                LOG.debug("Get Home Categories - DONE. Thread: [{}]", Thread.currentThread().getName());
                deferredResult.setResult(result);
            }
            @Override
            public void failed(Throwable e) {
                LOG.error("Unable to query home categories", e);
                List<CategoryDto> categories = Collections.emptyList(); 
                deferredResult.setResult(categories);
                deferredResult.setErrorResult(e);
            }
        });

        LOG.debug("Query home categories - EXIST. Thread: [{}]", Thread.currentThread().getName());
        return deferredResult;
    }
    
    @RequestMapping(value="products", method=RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ProductSearchResultDto> searchProducts(
            @RequestParam(value="terms", defaultValue = "") String terms,
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(value="pageSize", defaultValue = "15") int pageSize,
            @RequestParam(value="cat", required = false) String category,
            @RequestParam(value="m", required = false) String[] materials,
            @RequestParam(value="c", required = false) String[] colors,
            @RequestParam(value="sort", defaultValue="name") String sortColumn, 
            @RequestParam(value="dir", defaultValue="asc") String sortOrder) {
        
        LOG.debug("Search products - START. Thread: [{}]", Thread.currentThread().getName());
        final DeferredResult<ProductSearchResultDto> deferredResult = new DeferredResult<ProductSearchResultDto>(null, Collections.emptyList());

        ProductSearchDto searchRequest = new 
                ProductSearchDto.Builder()
                    .searchTerms(terms)
                    .page(page)
                    .pageSize(pageSize)
                    .category(category)
                    .materials(materials)
                    .colors(colors)
                    .sortColumn(sortColumn)
                    .sortOrder(sortOrder)
                .build();
        
        ActorUtils.callAsyncService(ServicesConstants.SERVICE_PRODUCT, new ProductsSearchRequest(searchRequest), new ExecutionCallback<ProductSearchResultDto>() {
            @Override
            public void done(ProductSearchResultDto result) {
                LOG.debug("Get Home Categories - DONE. Thread: [{}]", Thread.currentThread().getName());
                deferredResult.setResult(result);
            }
            @Override
            public void failed(Throwable e) {
                LOG.error("Unable to query home categories", e);
                ProductSearchResultDto result = new ProductSearchResultDto();
                List<ProductDto> products = Collections.emptyList();
                result.setProducts(products);
                result.setTotalCount(0);
                deferredResult.setResult(result);
                deferredResult.setErrorResult(e);
            }
        });
        
        LOG.debug("Search products - EXIST. Thread: [{}]", Thread.currentThread().getName());
        return deferredResult;
    }
}
