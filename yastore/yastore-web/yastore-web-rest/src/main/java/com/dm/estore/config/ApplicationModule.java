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
package com.dm.estore.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ResourceBundleMessageSource;

import akka.actor.ActorSystem;

import com.dm.estore.common.config.Cfg;
import com.dm.estore.common.constants.CommonConstants;

/**
 *
 * @author dmorozov
 */
@Configuration
@ComponentScan(basePackages = { "com.dm.estore.config.security" })
@ImportResource("classpath:/META-INF/spring/security-config.xml")
public class ApplicationModule {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationModule.class);
    
    @PostConstruct
    public void initialize() {
        LOG.info("Initialize '" + CommonConstants.App.CFG_APP_NAME + "' root context");
    }    
    
    @PreDestroy
    public void cleanUp() throws Exception {
        Cfg.instance().shutdown();
    }
    
    @Bean
    public Cfg applicationConfiguration() {
        return Cfg.Factory.createConfiguration(null);
    }

    @Bean(name = "actorSystem")
    public ActorSystem actorSystem(Cfg config) {
        // return Cfg.instance().actorSystem();
    	return config.actorSystem();
    }
    
    @Bean
	public ResourceBundleMessageSource messageSource() {
    	ResourceBundleMessageSource source = new ResourceBundleMessageSource();  
        source.setBasename("i18n/messages");  
        source.setDefaultEncoding(CommonConstants.i18n.DEFAULT_ENCODING);
        source.setUseCodeAsDefaultMessage(true);  
        return source;
	}
}
