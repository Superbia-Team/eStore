package com.dm.estore.search.repository;

import java.util.List;

import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dm.estore.search.dto.DocumentDto;

public interface DocumentsRepository extends SolrCrudRepository<DocumentDto, String> {
	
	public static final String TEMPLATE_CONTEXT_NAME = "templateDocuments";
	
	// Will execute count prior to determine total number of elements Derived Query will be
	// "q=name:<name>*&start=0&rows=<result of count query for q=name:<name>>"
	List<DocumentDto> findByNameStartingWith(String name);
}
