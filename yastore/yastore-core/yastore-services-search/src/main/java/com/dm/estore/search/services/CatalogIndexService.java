package com.dm.estore.search.services;

import com.dm.estore.core.models.Category;
import com.dm.estore.core.models.Product;

public interface CatalogIndexService {
	
	public static final String CONTEXT_NAME = "catalogIndexService";
	
	public void addToIndex(Product product);
	
	public void addToIndex(Category category);

    public void deleteProductFromIndex(Long id);
    
    public void deleteCategoryFromIndex(Long id);
}
