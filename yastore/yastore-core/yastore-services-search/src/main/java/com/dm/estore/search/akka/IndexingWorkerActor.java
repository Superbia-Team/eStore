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

package com.dm.estore.search.akka;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.UntypedActor;

import com.dm.estore.common.akka.events.ModelChangedEvent;
import com.dm.estore.common.akka.events.ModelChangedEvent.ChangeType;
import com.dm.estore.core.models.Category;
import com.dm.estore.core.models.Product;
import com.dm.estore.search.services.CatalogIndexService;

/**
 *
 * @author dmorozov
 */
@Component(IndexingWorkerActor.CONTEXT_NAME)
@Scope("prototype")
public class IndexingWorkerActor extends UntypedActor {
    
    public static final String CONTEXT_NAME = "indexingWorkerActor";
    public static final String ACTOR_NAME = IndexingWorkerActor.CONTEXT_NAME;
    
    //private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);
    private static final Logger LOG = LoggerFactory.getLogger(IndexingWorkerActor.class);
    
    @Resource(name = CatalogIndexService.CONTEXT_NAME)
    private CatalogIndexService productIndexService;
    
    @Override
    public void onReceive(Object message) throws Exception {
    	LOG.debug("ON MODEL CHANGE handler. Thread: [{}]", Thread.currentThread().getName());
        if (message instanceof ModelChangedEvent) {
            ModelChangedEvent event = (ModelChangedEvent) message;
            if (event.getModel() instanceof Product) {
            	Product product = (Product) event.getModel();
            	LOG.debug("Indexing for product '{}'. Thread: [{}]", product.getName(), Thread.currentThread().getName());
            	
            	if (event.getType() == ChangeType.CREATE || event.getType() == ChangeType.UPDATE) {
            		productIndexService.addToIndex(product);
            	} else if (event.getType() == ChangeType.DELETE) {
            		productIndexService.deleteProductFromIndex(product.getId());
            	}
            } else {
                Category category = (Category) event.getModel();
                LOG.debug("Indexing for category '{}'. Thread: [{}]", category.getName(), Thread.currentThread().getName());
                
                if (event.getType() == ChangeType.CREATE || event.getType() == ChangeType.UPDATE) {
                    productIndexService.addToIndex(category);
                } else if (event.getType() == ChangeType.DELETE) {
                    productIndexService.deleteCategoryFromIndex(category.getId());
                }
            }

        } else {
            unhandled(message);
        }
    }
}
