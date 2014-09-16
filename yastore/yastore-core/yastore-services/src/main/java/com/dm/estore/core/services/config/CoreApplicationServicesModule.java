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
package com.dm.estore.core.services.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.japi.EventBus;
import akka.japi.Creator;

import com.dm.estore.common.akka.ServicesConstants;
import com.dm.estore.common.akka.events.BusEvent;
import com.dm.estore.common.akka.events.ConfigurationChangedEvent;
import com.dm.estore.common.config.Cfg;
import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.core.actors.audit.AuditActors;
import com.dm.estore.core.actors.events.EventActors;
import com.dm.estore.core.actors.services.ServiceActors;

import static com.dm.estore.core.services.config.akka.AkkaSpringExtension.SpringExtProvider;

import com.dm.estore.core.services.config.akka.AkkaSpringExtension;
import com.dm.estore.core.services.config.akka.EventBusImpl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Spring Core services module configuration
 *
 * @author dmorozov
 */
@Configuration
@ComponentScan(basePackages = {
    "com.dm.estore.core.actors",
    "com.dm.estore.core.services",
    "com.dm.estore.services"
})
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class CoreApplicationServicesModule {
    
    private static final Logger LOG = LoggerFactory.getLogger(CoreApplicationServicesModule.class);

    @Autowired
    private ApplicationContext applicationContext;
    
    private final EventBus<BusEvent<?>, ActorRef, String> eventBus = new EventBusImpl();

    class TypedActorCreator<T extends Object> implements Creator<T> {
        
        public static final long serialVersionUID = 1L;
        
        private final String actorBeanName;

        TypedActorCreator(String actorBeanName) {
            this.actorBeanName = actorBeanName;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T create() throws Exception {
            return (T) applicationContext.getBean(actorBeanName);
        }
    }    
    
    @PostConstruct
    public void initialize() {
        LOG.info("Initialize '" + CommonConstants.App.CFG_APP_NAME + "' core services");
        
        initializeActorsSystem();
        initializeConfiguration();
    }
        
    protected void initializeActorsSystem() {
        final ActorSystem rootActorSystem = Cfg.instance().actorSystem();
        
        // initialize the application context in the Akka Spring Extension
        AkkaSpringExtension.SpringExtProvider.get(rootActorSystem).initialize(applicationContext);
    
        rootActorSystem.actorOf(SpringExtProvider.get(rootActorSystem).props(EventActors.CONTEXT_NAME), ServicesConstants.EVENTS_ROOT_PATH);
        rootActorSystem.actorOf(SpringExtProvider.get(rootActorSystem).props(AuditActors.CONTEXT_NAME), ServicesConstants.AUDIT_ROOT_PATH);
        rootActorSystem.actorOf(SpringExtProvider.get(rootActorSystem).props(ServiceActors.CONTEXT_NAME), ServicesConstants.SERVICES_ROOT_PATH);
    }

    protected void initializeConfiguration() {
        
        Cfg.instance().setCallback(new Cfg.ConfigurationChangeCallback() {
            @Override
            public void onChange() {
                eventBus.publish(new ConfigurationChangedEvent());
            }
        });
    }

    @Bean
    public EventBus<BusEvent<?>, ActorRef, String> eventBus() {
        return eventBus;
    }
}
