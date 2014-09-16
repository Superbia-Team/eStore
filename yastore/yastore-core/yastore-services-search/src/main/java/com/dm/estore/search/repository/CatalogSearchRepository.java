package com.dm.estore.search.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.Boost;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Highlight;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dm.estore.search.dto.CatalogItemDto;
import com.dm.estore.search.dto.CatalogItemType;

public interface CatalogSearchRepository extends CatalogCustomRepository,
		SolrCrudRepository<CatalogItemDto, String> {

	public static final String TEMPLATE_CONTEXT_NAME = "templateCatalog";

	// Will execute count prior to determine total number of elements Derived
	// Query will be
	// "q=name:<name>*&start=0&rows=<result of count query for q=name:<name>>"
	List<CatalogItemDto> findByNameStartingWith(String name);

	Page<CatalogItemDto> findByPopularity(Integer popularity, Pageable page);

	Page<CatalogItemDto> findByNameOrDescription(@Boost(2) String name,
			String description, Pageable page);

	@Highlight
	HighlightPage<CatalogItemDto> findByNameIn(Collection<String> name, Pageable page);

	@Query(value = "name:?0")
	@Facet(fields = { "category" }, limit = 20)
	FacetPage<CatalogItemDto> findByNameAndFacetOnCategory(String name, Pageable page);
	
	@Query("name:*?0* OR summary:*?0* OR description:*?0*")
	@Facet(fields = { "category", "tag" }, limit = 20)
    FacetPage<CatalogItemDto> search(String searchTerm, Pageable page);
	
	List<CatalogItemDto> findByActiveTrue(Sort sort);
	
	List<CatalogItemDto> findByCategoriesAndDocumentType(String categories, CatalogItemType documentType, Sort sort);
}
