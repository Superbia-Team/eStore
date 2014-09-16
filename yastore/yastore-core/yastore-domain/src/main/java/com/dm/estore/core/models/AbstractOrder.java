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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.springframework.util.Assert;

@MappedSuperclass
public class AbstractOrder extends AbstractEntity {

	private static final long serialVersionUID = 446791082390641550L;
	
	@Column(nullable = false, insertable = true, updatable = false)
	private String code;

	@ManyToOne(optional = false)
	private User customer;

	@ManyToOne
	private Address billingAddress;

	@ManyToOne
	private Address shippingAddress;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "order_id")
	// @OrderBy("lineNumber")  
	private Set<LineItem> lineItems = new HashSet<LineItem>();

	/**
	 * Creates a new {@link AbstractOrder} for the given {@link User}.
	 * 
	 * @param customer must not be {@literal null}.
	 * @param shippingAddress must not be {@literal null}.
	 */
	public AbstractOrder(User customer, Address shippingAddress) {
		this(customer, shippingAddress, null);
	}

	/**
	 * Creates a new {@link AbstractOrder} for the given customer, shipping and billing {@link Address}.
	 * 
	 * @param customer must not be {@literal null}.
	 * @param shippingAddress must not be {@literal null}.
	 * @param billingAddress can be {@@iteral null}.
	 */
	public AbstractOrder(User customer, Address shippingAddress, Address billingAddress) {

		Assert.notNull(customer);
		Assert.notNull(shippingAddress);

		this.customer = customer;
		this.shippingAddress = shippingAddress.getCopy();
		this.billingAddress = billingAddress == null ? null : billingAddress.getCopy();
	}

	protected AbstractOrder() {

	}

	/**
	 * Adds the given {@link LineItem} to the {@link AbstractOrder}.
	 * 
	 * @param lineItem
	 */
	public void add(LineItem lineItem) {
		this.lineItems.add(lineItem);
	}

	/**
	 * Returns the {@link User} who placed the {@link AbstractOrder}.
	 * 
	 * @return
	 */
	public User getCustomer() {
		return customer;
	}

	/**
	 * Returns the billing {@link Address} for this order.
	 * 
	 * @return
	 */
	public Address getBillingAddress() {
		return billingAddress != null ? billingAddress : shippingAddress;
	}

	/**
	 * Returns the shipping {@link Address} for this order;
	 * 
	 * @return
	 */
	public Address getShippingAddress() {
		return shippingAddress;
	}

	/**
	 * Returns all {@link LineItem}s currently belonging to the {@link AbstractOrder}.
	 * 
	 * @return
	 */
	public Set<LineItem> getLineItems() {
		return Collections.unmodifiableSet(lineItems);
	}

	/**
	 * Returns the total of the {@link AbstractOrder}.
	 * 
	 * @return
	 */
	public BigDecimal getTotal() {

		BigDecimal total = BigDecimal.ZERO;

		for (LineItem item : lineItems) {
			total = total.add(item.getTotal());
		}

		return total;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public void setLineItems(Set<LineItem> lineItems) {
		this.lineItems = lineItems;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
