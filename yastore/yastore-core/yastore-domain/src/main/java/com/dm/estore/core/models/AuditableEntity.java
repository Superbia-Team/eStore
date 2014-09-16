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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Base class to derive entity classes from.
 * 
 * @author dmorozov
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@CreatedDate
    @NotNull
    @Column(name = "CREATED_DATE")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime createdDate = DateTime.now();	
	
	@CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY")
	private User createdBy;

	
	@LastModifiedDate
    @NotNull
    @Column(name = "LAST_MODIFIED_DATE")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")	
	private DateTime lastModifiedDate = DateTime.now();

	@LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAST_MODIFIED_BY")
	private User lastModifiedBy;	

	public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public DateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(DateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public User getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(User lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
}
