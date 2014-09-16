package com.dm.estore.search.config.solr;

import java.io.Serializable;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.springframework.data.solr.repository.support.SolrRepositoryFactoryBean;
import org.springframework.data.solr.server.SolrServerFactory;
import org.springframework.data.solr.server.support.SolrServerUtils;

public class CustomSolrRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>  extends SolrRepositoryFactoryBean<T, S, ID> {
	
	@Resource
	private SolrServer solrServer;
	
	@Resource
	private SolrServerFactory solrServerFactory;
	
	@Autowired
	ApplicationContext applicationContext;
	
    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
    	SolrTemplate template = new SolrTemplate(solrServerFactory);
    	template.setApplicationContext(applicationContext);
		template.afterPropertiesSet();
    	return new CustomSolrRepositoryFactory<T, ID>(template);
    }
 
    private static class CustomSolrRepositoryFactory<T, ID extends Serializable> extends SolrRepositoryFactory {
 
        private final SolrTemplate solrTemplate;
 
        public CustomSolrRepositoryFactory(SolrTemplate solrTemplate) {
            super(solrTemplate);
            this.solrTemplate = solrTemplate;
        }
        
        @Override
    	protected Object getTargetRepository(RepositoryMetadata metadata) {
        	solrTemplate.setSolrCore(SolrServerUtils.resolveSolrCoreName(metadata.getDomainType()));
        	return super.getTargetRepository(metadata);
    	}
    }
}
