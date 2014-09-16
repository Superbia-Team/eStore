package com.dm.estore.common.dto.requests.shoppingCart;


public class AddProductRequest extends ShoppingCartUpdate {

	private static final long serialVersionUID = -4264274976053214675L;

	private final String catalogNumber;
	
	private final int quantity;
	
	public AddProductRequest(final String catalogNumber, final int quantity) {
		super(ShoppingCartUpdate.UpdateType.ADD_PRODUCT);
		
		this.catalogNumber = catalogNumber;
		this.quantity = quantity;
	}

	public String getCatalogNumber() {
		return catalogNumber;
	}

	public int getQuantity() {
		return quantity;
	}
}
