package com.dm.estore.search.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;

import com.dm.estore.search.dto.CatalogItemDto;

public interface CatalogCustomRepository {
	
	Page<CatalogItemDto> findProductsByCustomImplementation(final String value, final Pageable page);
	
	Page<CatalogItemDto> searchCustom(final String searchTerm, final Pageable page);
	
	public void updateLocalizedProperties(final CatalogItemDto catalogItem);
	
	FacetPage<CatalogItemDto> searchProducts(final String searchTerms, final String category, 
	        final String[] materials, final String[] colors,
	        final Pageable page);
}
