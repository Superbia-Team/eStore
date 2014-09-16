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

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.core.models.ShoppingCart;

/**
 * Spring REST API module configuration
 *
 * @author dmorozov
 */
@Configuration
@ComponentScan(basePackages = { "com.dm.estore.controllers" })
@EnableWebMvc
public class RestAPIModule extends RepositoryRestMvcConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(RestAPIModule.class);

    @PostConstruct
    public void initialize() {
        LOG.info("Initialize '" + CommonConstants.App.CFG_APP_NAME + "' REST API");
    }
    
    @Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(30*1000L);
	}
    
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new MappingJackson2HttpMessageConverter());
	}    

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
    	LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
    	interceptor.setParamName(CommonConstants.i18n.PARAM_CHANGE_LOCALE);
    	return interceptor;
    }
    
    @Bean
    public SessionLocaleResolver localeResolver() {
    	SessionLocaleResolver resolver = new SessionLocaleResolver();
    	resolver.setDefaultLocale(CommonConstants.i18n.DEFAULT_LOCALE);
    	return resolver;
    }    
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

		// Extra argument resolvers (the default ones are added as well).
        // argumentResolvers.add(new PageableArgumentResolver());
    }
    
    /*
	import com.fasterxml.jackson.databind.Module;
	import com.fasterxml.jackson.databind.Module.SetupContext;
	import com.fasterxml.jackson.databind.ObjectMapper;
	import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
	import com.fasterxml.jackson.databind.module.SimpleModule;

	@Override
	protected void configureJacksonObjectMapper(ObjectMapper objectMapper) {
		objectMapper.registerModule(new SimpleModule(CommonConstants.App.CFG_APP_NAME + "_jackson_module") {
			private static final long serialVersionUID = 1L;

			@Override
			public void setupModule(SetupContext context) {
				context.addAbstractTypeResolver(new SimpleAbstractTypeResolver()
						.addMapping(MyInterface.class, MyInterfaceImpl.class));
			}
		});
	}
	*/

    @Override
    @SuppressWarnings("deprecation")
	protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.setResourceMappingForDomainType(ShoppingCart.class)
					.addResourceMappingFor("guid")
					.setPath("code");
	}
	
	@Bean
	public ResourceProcessor<Resource<ShoppingCart>> personResourceProcessor() {
		return new ResourceProcessor<Resource<ShoppingCart>>() {
			RepositoryRestConfiguration config = config();

			@Override
			public Resource<ShoppingCart> process(Resource<ShoppingCart> resource) {
				System.out.println("processing " + resource);
				System.out.println("url: " + config.getBaseUri().toString());
				resource.add(new Link("http://host:port/path", "myrel"));
				return resource;
			}
		};
	}
	
	@Bean
	public ResourceProcessor<RepositoryLinksResource> rootLinksResourceProcessor() {
		return new ResourceProcessor<RepositoryLinksResource>() {
			@Override
			public RepositoryLinksResource process(RepositoryLinksResource resource) {
				resource.add(new Link("href", "rel"));
				return resource;
			}
		};
	}
}
