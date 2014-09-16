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
package com.dm.estore.core.springdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

/**
 * Dynamic entities resolver
 * 
 * @author dmorozov
 */
public class ExtendablePersistenceUnitPostProcessor implements
		PersistenceUnitPostProcessor {
	
	private static final Logger LOG = LoggerFactory.getLogger(ExtendablePersistenceUnitPostProcessor.class);
	
	private static final String TEST_RESOURCES_PREFIX = "test_";

	Map<String, List<String>> puiClasses = new HashMap<String, List<String>>();

	@Override
	public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
		
		if (!pui.getPersistenceUnitName().startsWith(TEST_RESOURCES_PREFIX)) {
			List<String> classes = puiClasses.get(pui.getPersistenceUnitName());
			if (classes == null) {
				classes = new ArrayList<String>();
				puiClasses.put(pui.getPersistenceUnitName(), classes);
			}
			
			classes.addAll(resolveEntities());
			pui.getManagedClassNames().addAll(classes);
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Resolved JPA entities:");
				for (String className : pui.getManagedClassNames()) {
					LOG.debug("    " + className);
				}
			}
		}
	}
	
	/**
	 * Resolve all annotated JPA entities and add them dynamically.
	 * Note: If there is entity which extends another entity only descendant entity will 
	 * be added. This is simple way to extend OOB entities.
	 * 
	 * TODO: Do we need another mechanism to support, for example, multiple data sources?
	 *  
	 * @return List of JPA entity classes
	 */
	private List<String> resolveEntities() {
		
		List<Class<?>> entitiesClasses = new ArrayList<Class<?>>();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Embeddable.class));
        for (BeanDefinition bd : scanner.findCandidateComponents("com.dm.estore")) {
        	try {
                Class<?> clazz = ExtendablePersistenceUnitPostProcessor.class.getClassLoader().loadClass(bd.getBeanClassName());
                if (!entitiesClasses.contains(clazz)) {
                	Iterator<Class<?>> i = entitiesClasses.iterator();
                	while(i.hasNext()) {
                		Class<?> c = i.next();
                		if (c.isAssignableFrom(clazz)) {
                            i.remove();
                            break;
                        }
                	}
                	
               		entitiesClasses.add(clazz);
                }
                
            } catch (ClassNotFoundException ex) {
                LOG.error("Unable to load class: " + bd.getBeanClassName(), ex);
            }
        }
        
        List<String> entities = new ArrayList<String>();
        for (Class<?> c : entitiesClasses) {
        	String classPath = c.getCanonicalName();
        	entities.add(classPath);
        }
        
		return entities;
	}
}
