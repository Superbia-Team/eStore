package com.dm.estore.search.repository.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.DisMaxParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.PartialUpdate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.SolrResultPage;

import com.dm.estore.search.dto.CatalogItemDto;
import com.dm.estore.search.dto.CatalogItemType;
import com.dm.estore.search.repository.CatalogCustomRepository;
import com.dm.estore.search.repository.CatalogSearchRepository;

public class CatalogSearchRepositoryImpl implements CatalogCustomRepository {

	private SolrTemplate solrTemplate;
	
	private static final String OP_OR = " OR ";
	private static final String OP_AND = " AND ";
	private static final String OP_EQ = ":";
	private static final String QUERY_PRODUCT_TYPE = CatalogItemDto.FIELD_DOCUMENT_TYPE + OP_EQ + CatalogItemType.product;
	
	@Override
	public Page<CatalogItemDto> findProductsByCustomImplementation(String value, Pageable page) {
		Query query = new SimpleQuery(new SimpleStringCriteria("name:" + value)).setPageRequest(page);
	    return solrTemplate.queryForPage(query, CatalogItemDto.class);
	}
	
	@Resource(name = CatalogSearchRepository.TEMPLATE_CONTEXT_NAME)
	public void setSolrTemplate(SolrTemplate solrTemplate) {
		this.solrTemplate = solrTemplate;
	}
	
	@Override
	public Page<CatalogItemDto> searchCustom(String searchTerm, Pageable page) {
	    assert searchTerm != null; // "Search terms cannot be null!");
	    final SolrQuery query = new SolrQuery();

	    // 2 edismax query setup
	    query.set("q", searchTerm);
	    query.set(DisMaxParams.QF, "tag^80.0 name^20.0 summary^2.0 description^2.0");
	    query.set("defType", "edismax");

	    // 3 paging
	    query.setStart(page.getOffset());
	    query.setRows(page.getPageSize());

	    try {
	        return execute(query, page);
	    } catch (SolrServerException ex) {
	        throw (SolrTemplate.getExceptionTranslator().translateExceptionIfPossible(new RuntimeException(ex))); // 5
	    }
	}
	
	private Page<CatalogItemDto> execute(final SolrQuery query, final Pageable page) throws SolrServerException {
		final QueryResponse resp = solrTemplate.getSolrServer().query(query);
		final List<CatalogItemDto> beans = solrTemplate.convertQueryResponseToBeans(resp, CatalogItemDto.class); // 6
		return new SolrResultPage<CatalogItemDto>(beans, page, resp.getResults().getNumFound(), resp.getResults().getMaxScore()); // 7
	}
	
	public long count(String searchTerm) {
        String[] words = searchTerm.split(" ");
        Criteria conditions = createSearchConditions(words);
        SimpleQuery countQuery = new SimpleQuery(conditions);
 
        return solrTemplate.count(countQuery);
    }
	
	public List<CatalogItemDto> sampleSearch2(String searchTerm, final Pageable page) {
		String[] words = searchTerm.split(" ");
		 
        Criteria conditions = createSearchConditions(words);
        SimpleQuery search = new SimpleQuery(conditions);
        
        search.addSort(new Sort(Sort.Direction.DESC, CatalogItemDto.FIELD_NAME));
        search.setPageRequest(page);
 
        Page<CatalogItemDto> results = solrTemplate.queryForPage(search, CatalogItemDto.class);
        return results.getContent();
	}
	
	private Criteria createSearchConditions(String[] words) {
        Criteria conditions = null;
 
        for (String word: words) {
        	Criteria condition = new Criteria(CatalogItemDto.FIELD_NAME).contains(word)
        		.or(new Criteria(CatalogItemDto.FIELD_SUMMARY).contains(word)
                .or(new Criteria(CatalogItemDto.FIELD_DESCRIPTION).contains(word))); 
        	
            if (conditions == null) {
                conditions = condition;
            }
            else {
                conditions = conditions.or(condition);
            }
        }
 
        return conditions;
    }
	
	@Override
	public void updateLocalizedProperties(final CatalogItemDto catalogItem) {
		PartialUpdate update = new PartialUpdate(CatalogItemDto.FIELD_ID, catalogItem.getId());
		update.add(CatalogItemDto.FIELD_DOCUMENT_ID, catalogItem.getDocumentId());
		update.add(CatalogItemDto.FIELD_DOCUMENT_TYPE, catalogItem.getDocumentType().name());
		
		if (catalogItem.getNames().size() > 0) {
			for (Map.Entry<String, String> entry : catalogItem.getNames().entrySet()) {
				update.add(CatalogItemDto.FIELD_NAMES + entry.getKey(), entry.getValue());
			}
		}
		if (catalogItem.getSummaries().size() > 0) {
			for (Map.Entry<String, String> entry : catalogItem.getSummaries().entrySet()) {
				update.add(CatalogItemDto.FIELD_SUMMARIES + entry.getKey(), entry.getValue());
			}
		}
		if (catalogItem.getDescriptions().size() > 0) {
			for (Map.Entry<String, String> entry : catalogItem.getDescriptions().entrySet()) {
				update.add(CatalogItemDto.FIELD_DESCRIPTIONS + entry.getKey(), entry.getValue());
			}
		}
 
        solrTemplate.saveBean(update);
        solrTemplate.commit();
	}
	
	private String createSearchTermsQuery(String searchQuery) {
	    if (!StringUtils.isEmpty(searchQuery)) {
    	    StringBuilder sb = new StringBuilder();
    	    
    	    String[] terms = searchQuery.split(" ");
    	    for (String term : terms) {
    	        if (sb.length() > 0) sb.append(OP_OR);
    	        
    	        sb.append("(")
    	            .append(CatalogItemDto.FIELD_NAME).append(OP_EQ).append(term).append(Criteria.WILDCARD)
    	            .append(OP_OR)
    	            .append(CatalogItemDto.FIELD_NAME).append(OP_EQ).append(term)
    	            .append(OP_OR)
                    .append(CatalogItemDto.FIELD_SUMMARY).append(OP_EQ).append(term).append(Criteria.WILDCARD)
                    .append(OP_OR)
                    .append(CatalogItemDto.FIELD_SUMMARY).append(OP_EQ).append(term)
                    .append(OP_OR)
                    .append(CatalogItemDto.FIELD_DESCRIPTION).append(OP_EQ).append(term).append(Criteria.WILDCARD)
                    .append(OP_OR)
                    .append(CatalogItemDto.FIELD_DESCRIPTION).append(OP_EQ).append(term)
                    .append(OP_OR)
                    .append(CatalogItemDto.FIELD_TAG).append(OP_EQ).append(term).append(Criteria.WILDCARD)
                    .append(OP_OR)
                    .append(CatalogItemDto.FIELD_TAG).append(OP_EQ).append(term)
                    .append(")");
    	    }
    	    
    	    return QUERY_PRODUCT_TYPE + OP_AND + " (" + sb.toString() + ")";
	    }
	    
	    return QUERY_PRODUCT_TYPE;
	}
	
    @Override
    public FacetPage<CatalogItemDto> searchProducts(final String searchTerms, final String category, 
            final String[] materials, final String[] colors,
            final Pageable page) {
        
        assert (!StringUtils.isEmpty(searchTerms) || StringUtils.isEmpty(category) ||
                materials != null && materials.length > 0 ||
                colors != null && colors.length > 0);

        // Price:[10 TO 40] 
        // http://localhost:8983/solr/catalog/select?q=documentType%3Aproduct&wt=json&indent=true&facet=true&facet.field=category&facet.field=variantGroup&facet.field=variantSubGroup&fq=category:ANT101&fq=variantGroup:gold
        
        FacetQuery query = new SimpleFacetQuery(new SimpleStringCriteria(createSearchTermsQuery(searchTerms)));
        query.setFacetOptions(
                new FacetOptions(CatalogItemDto.FIELD_CATEGORY,
                        CatalogItemDto.FIELD_VARIANT_GROUP,
                        CatalogItemDto.FIELD_VARIANT_SUBGROUP)
                    .setFacetMinCount(0)
                    .setFacetLimit(100));
        query.setPageRequest(page);
        
        if (!StringUtils.isEmpty(category)) {
            query.addFilterQuery(new SimpleQuery(CatalogItemDto.FIELD_CATEGORY + OP_EQ + category));
        }
        if (materials != null && materials.length > 0) {
            for (String material : materials) {
                query.addFilterQuery(new SimpleQuery(CatalogItemDto.FIELD_VARIANT_GROUP + OP_EQ + material));
            }
        }
        if (colors != null && colors.length > 0) {
            for (String color : colors) {
                query.addFilterQuery(new SimpleQuery(CatalogItemDto.FIELD_VARIANT_SUBGROUP + OP_EQ + color));
            }
        }
    
        return solrTemplate.queryForFacetPage(query, CatalogItemDto.class);
    }
}
