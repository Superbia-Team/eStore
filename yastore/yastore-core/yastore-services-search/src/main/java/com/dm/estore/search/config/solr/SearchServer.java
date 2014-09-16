package com.dm.estore.search.config.solr;

import org.apache.commons.configuration.Configuration;
import org.springframework.data.solr.server.SolrServerFactory;

public interface SearchServer {
	
    public static final String SEARCH_TEMPLATE = "searchTemplate";
    
    public static final String SEARCH_INDEX_TEMPLATE = "searchIndexTemplate";
	
	public enum DeploymentType {
		classic, solr
	}
	public enum ServerType {
		embedded, cloud
	}
	
	SolrServerFactory getServerFactory();
	
	void start(final String homeFolder, Configuration configuration);
	
	void shutdown();
}
