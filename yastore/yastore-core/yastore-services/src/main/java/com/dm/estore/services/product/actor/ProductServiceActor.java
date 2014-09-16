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

package com.dm.estore.services.product.actor;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.dm.estore.common.akka.ServicesConstants;
import com.dm.estore.common.dto.requests.HomeCategoriesRequest;
import com.dm.estore.common.dto.requests.ProductsSearchRequest;
import com.dm.estore.common.dto.requests.SearchCategoriesRequest;
import com.dm.estore.services.product.ProductService;
import com.dm.estore.services.search.CatalogSearchService;

/**
 *
 * @author dmorozov
 */
@Component(ProductServiceActor.CONTEXT_NAME)
@Scope("prototype")
public class ProductServiceActor extends UntypedActor {
    
    public static final String CONTEXT_NAME = ServicesConstants.SERVICE_PRODUCT;
    public static final String ACTOR_NAME = ProductServiceActor.CONTEXT_NAME;
    
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);
    
    @Resource(name = ProductService.CONTEXT_NAME)
    private ProductService productService;
    
    @Resource(name = CatalogSearchService.CONTEXT_NAME)
    private CatalogSearchService productSearchService;
    
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof HomeCategoriesRequest) {
            LOG.debug("ProductServiceActor -> Get Home Categories. Thread: [{}]", Thread.currentThread().getName());
            getSender().tell(productSearchService.findHomeCategories(), getSelf());
        } else if (message instanceof ProductsSearchRequest) {
            getSender().tell(productSearchService.findProducts(((ProductsSearchRequest) message).getRequest()), getSelf());
        } else if (message instanceof SearchCategoriesRequest) {
            getSender().tell(productSearchService.findCategories(((SearchCategoriesRequest) message).getRootCategory()), getSelf());
        } else {
            unhandled(message);
        }
    }
}
