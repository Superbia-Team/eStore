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
package com.dm.estore.common.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Helper class to dynamically autowire properties for non Spring beans.
 * It can be useful, for example, in JPA listeners.
 * !!!Important: make sure that AutowireHelper will be created by Spring BEFORE using it:
 * 
 *  @Bean
 *  public AutowireHelper autowireHelper() {
 *  	return AutowireHelper.getInstance();
 *  }
 * 
 * @author dmorozov
 *
 */
public class AutowireHelper implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(AutowireHelper.class);
	private static final AutowireHelper INSTANCE = new AutowireHelper();
	private ApplicationContext applicationContext;

	private AutowireHelper() {
	}

	/**
	 * Tries to autowire the specified instance of the class if one of the
	 * specified beans which need to be autowired are null.
	 * 
	 * @param classToAutowire
	 *            the instance of the class which holds @Autowire annotations
	 * @param beansToAutowireInClass
	 *            the beans which have the @Autowire annotation in the specified
	 *            {#classToAutowire}
	 */
	public static void autowire(Object classToAutowire, Object... beansToAutowireInClass) {
		for (Object bean : beansToAutowireInClass) {
			if (bean == null) {
				try {
					AutowireHelper.getInstance().applicationContext.getAutowireCapableBeanFactory().autowireBean(classToAutowire);
				} catch (Exception e) {
					LOG.error("Unable to wire bean of a class: " + beansToAutowireInClass.toString());
				}
			}
		}
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * @return the singleton instance.
	 */
	public static AutowireHelper getInstance() {
		return INSTANCE;
	}
}
