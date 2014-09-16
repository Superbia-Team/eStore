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

import javax.persistence.Entity;

/**
 * Shopping cart model.
 * 
 * @author dmorozov
 *
 */
@Entity
public class ShoppingCart extends Order {

	private static final long serialVersionUID = 446791082390641550L;

	/**
	 * Creates a new {@link ShoppingCart} for the given {@link User}.
	 * 
	 * @param customer must not be {@literal null}.
	 * @param shippingAddress must not be {@literal null}.
	 */
	public ShoppingCart(User customer, Address shippingAddress) {
		super(customer, shippingAddress, null);
	}

	/**
	 * Creates a new {@link ShoppingCart} for the given customer, shipping and billing {@link Address}.
	 * 
	 * @param customer must not be {@literal null}.
	 * @param shippingAddress must not be {@literal null}.
	 * @param billingAddress can be {@@iteral null}.
	 */
	public ShoppingCart(User customer, Address shippingAddress, Address billingAddress) {
		super(customer, shippingAddress, billingAddress);
	}
}
