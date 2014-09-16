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
package com.dm.estore.core.models;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;

import com.dm.estore.common.exceptions.ImmutableStateException;

/**
 * Base class to derive entity classes from.
 * 
 * @author dmorozov
 */
@MappedSuperclass
public abstract class AbstractEntity extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	/**
	 * Returns the identifier of the entity.
	 * 
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
				
		if (this == obj) {
			return true;
		}

		if (this.id == null || obj == null
				|| !(this.getClass().equals(obj.getClass()))) {
			return false;
		}

		AbstractEntity that = (AbstractEntity) obj;
		return this.id.equals(that.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}

	public <T> T cloneTo() {
		try {
			@SuppressWarnings("unchecked")
			T newClone = (T) BeanUtils.cloneBean(this); 
			return newClone;

		} catch (IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException e) {

			throw new ImmutableStateException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <Y> Y cloneTo(Y targetBean) {
		try {
			Map<String, String> sourceBeanProperties = BeanUtils.describe(this);
			Map<String, String> targetBeanProperties = BeanUtils.describe(targetBean);
			Collection<String> commonProperties = CollectionUtils.intersection(sourceBeanProperties.keySet(), targetBeanProperties.keySet());
			for (String property : commonProperties) {
				BeanUtils.copyProperty(this, property, targetBean);
			}

			return targetBean;

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {

			throw new ImmutableStateException(e);
		}
	}
}
