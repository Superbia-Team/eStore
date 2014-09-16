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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

@Entity
public class Customer extends User {

	private static final long serialVersionUID = -1424134944321853759L;

	@Column
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime birthday;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "customer_id")
	private Set<Address> addresses = new HashSet<Address>();
	
	/**
	 * Adds the given {@link Address} to the {@link User}.
	 * 
	 * @param address must not be {@literal null}.
	 */
	public void add(Address address) {

		Assert.notNull(address);
		this.addresses.add(address);
	}
	
	/**
	 * Return the {@link User}'s addresses.
	 * 
	 * @return
	 */
	public Set<Address> getAddresses() {
		return Collections.unmodifiableSet(addresses);
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

	public DateTime getBirthday() {
		return birthday;
	}

	public void setBirthday(DateTime birthday) {
		this.birthday = birthday;
	}
}
