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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.util.Assert;

/**
 * A customer.
 * 
 * @author dmorozov
 */
@Entity
@Table(name = "USERS")
public class User extends AbstractEntity {

	private static final long serialVersionUID = -7626603700871380779L;

	private String firstName, lastName, middleName;
	
	@Column(unique = true)
	private String userName;
	
	@Column(unique = true)
	private EmailAddress emailAddress;

	@ElementCollection
	private Map<String, String> attributes = new HashMap<String, String>();
	
	@Column(insertable = false, updatable = false)
	private String dtype;

	/**
	 * Creates a new {@link User} from the given firstname and lastname.
	 * 
	 * @param firstname must not be {@literal null} or empty.
	 * @param lastname must not be {@literal null} or empty.
	 */
	public User(String firstname, String lastname) {
		super();
		
		Assert.hasText(firstname);
		Assert.hasText(lastname);

		this.firstName = firstname;
		this.lastName = lastname;
	}

	public User() {
		super();
	}
	
	public User(String userName) {
		super();
		this.userName = userName;
	}

	/**
	 * Returns the {@link EmailAddress} of the {@link User}.
	 * 
	 * @return
	 */
	public EmailAddress getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets the {@link User}'s {@link EmailAddress}.
	 * 
	 * @param emailAddress must not be {@literal null}.
	 */
	public void setEmailAddress(EmailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDtype() {
		return dtype;
	}

	public void setDtype(String dtype) {
		this.dtype = dtype;
	}
}