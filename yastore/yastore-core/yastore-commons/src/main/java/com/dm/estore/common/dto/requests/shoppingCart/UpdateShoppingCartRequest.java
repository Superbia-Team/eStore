package com.dm.estore.common.dto.requests.shoppingCart;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class UpdateShoppingCartRequest implements Serializable {

	private static final long serialVersionUID = 5402385914361592335L;

	private final String code;
	
	private final List<ShoppingCartUpdate> updates;
	
	public UpdateShoppingCartRequest(final String shoppingCartCode, List<ShoppingCartUpdate> updates) {
		this.code = shoppingCartCode;
		this.updates = Collections.unmodifiableList(updates);
	}

	public String getCode() {
		return code;
	}

	public List<ShoppingCartUpdate> getUpdates() {
		return updates;
	}
}
