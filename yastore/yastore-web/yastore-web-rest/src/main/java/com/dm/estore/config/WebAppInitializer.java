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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.filters.ApplicationFilter;

/**
 * @author dmorozov
 */
public class WebAppInitializer implements WebApplicationInitializer {
    
    private static final Logger LOG = LoggerFactory.getLogger(WebAppInitializer.class);

    private static final String APP_FILTER_MAPPING = "*";
    private static final String APP_FILTER_NAME = "application-filter";
    private static final String SECURITY_FILTER_NAME = "springSecurityFilterChain";
    private static final String TEST_RESOURCES_PREFIX = "test";
    
    private static final String REST_SERVLET_MAPPING = "/api/*";

    private static List<String> excludeFromAutoSearch = new ArrayList<String>(){
		private static final long serialVersionUID = 1L;
	{
    	add(ApplicationModule.class.getCanonicalName());
    	add(RestAPIModule.class.getCanonicalName());
    }};

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        LOG.info("Initialize '" + CommonConstants.App.CFG_APP_NAME + "' web application ...");
        
        registerListener(servletContext);
        registerServlets(servletContext);
        registerFilters(servletContext);
    }

    private void registerListener(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext rootContext = createContext(ApplicationModule.class);
        
        // find all classes marked as @Configuration in classpath
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Configuration.class));
        //TypeFilter tf = new AssignableTypeFilter(CLASS_YOU_WANT.class);
        //s.addIncludeFilter(tf);
        //s.scan("package.you.want1", "package.you.want2");       
        //String[] beans = bdr.getBeanDefinitionNames();
        for (BeanDefinition bd : scanner.findCandidateComponents("com.dm.estore")) {
        	final String beanClassName = bd.getBeanClassName();
        	final String simpleName = ClassUtils.getShortCanonicalName(beanClassName);
            if (!excludeFromAutoSearch.contains(beanClassName) 
            		&& !simpleName.toLowerCase().startsWith(TEST_RESOURCES_PREFIX)) {
                LOG.warn("Load configuration from: " + bd.getBeanClassName());
                try {
                    Class<?> clazz = WebAppInitializer.class.getClassLoader().loadClass(bd.getBeanClassName());
                    rootContext.register(clazz);
                } catch (ClassNotFoundException ex) {
                    LOG.error("Unable to load class: " + bd.getBeanClassName(), ex);
                }
            }
        }
        rootContext.refresh();
   
        servletContext.addListener(new ContextLoaderListener(rootContext));
    }
    
    private void registerServlets(ServletContext servletContext) {
    	// Enable Spring Data REST in the DispatcherServlet
        AnnotationConfigWebApplicationContext webCtx = new AnnotationConfigWebApplicationContext();
        webCtx.register(RestAPIModule.class);
        
        // REST API dispatcher
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webCtx);
        ServletRegistration.Dynamic restDispatcher = servletContext.addServlet("restAPI", dispatcherServlet);
        restDispatcher.setLoadOnStartup(1);
        restDispatcher.setAsyncSupported(true);
        restDispatcher.addMapping(REST_SERVLET_MAPPING);
    }

    private void registerFilters(ServletContext servletContext) {
    	FilterRegistration.Dynamic securityFilter = servletContext.addFilter(SECURITY_FILTER_NAME, new DelegatingFilterProxy());
    	securityFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, REST_SERVLET_MAPPING);
    	securityFilter.setAsyncSupported(true);
        
        FilterRegistration.Dynamic applicationFilter = servletContext.addFilter(APP_FILTER_NAME, new ApplicationFilter());
        applicationFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), false, APP_FILTER_MAPPING);
        applicationFilter.setAsyncSupported(true);
    }

    private AnnotationConfigWebApplicationContext createContext(final Class<?>... modules) {
        AnnotationConfigWebApplicationContext _ctx = new AnnotationConfigWebApplicationContext();
        _ctx.register(modules);
        return _ctx;
    }
}
