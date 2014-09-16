package com.dm.estore.common.dto.requests.shoppingCart;

import java.io.Serializable;

public class ShoppingCartUpdate implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public enum UpdateType {
		ADD_PRODUCT, REMOVE_PRODUCT, APPLY_DISCOUNT, TAKEOFF_DISCOUNT 
	}
	
	private final UpdateType type;

	public ShoppingCartUpdate(final UpdateType type) {
		this.type = type;
	}
	
	public UpdateType getType() {
		return type;
	}
}
