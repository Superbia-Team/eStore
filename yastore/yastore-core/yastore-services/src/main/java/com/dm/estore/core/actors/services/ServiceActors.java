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

package com.dm.estore.core.actors.services;

import static com.dm.estore.core.services.config.akka.AkkaSpringExtension.SpringExtProvider;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;

import com.dm.estore.core.actors.RootAppActor;
import com.dm.estore.services.product.actor.ProductServiceActor;

/**
 *
 * @author dmorozov
 */
@Component(ServiceActors.CONTEXT_NAME)
@Scope("prototype")
public class ServiceActors extends RootAppActor {
    
    public static final String CONTEXT_NAME = "serviceActors";
    
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);
    
    @Resource(name = "actorSystem")
    private ActorSystem actorSystem;
    
    @Override
    public void preStart() throws Exception {
        super.preStart();
        
        // Register ProductServiceActor
        Props actorProps = SpringExtProvider.get(getContext().system()).props(ProductServiceActor.CONTEXT_NAME);
        getContext().actorOf(FromConfig.getInstance().props(actorProps), ProductServiceActor.ACTOR_NAME);        
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
    	LOG.debug("Received service message");
        super.onReceive(message);
    }

	@Override
	protected ActorSystem actorSystem() {
		return actorSystem;
	}
}
