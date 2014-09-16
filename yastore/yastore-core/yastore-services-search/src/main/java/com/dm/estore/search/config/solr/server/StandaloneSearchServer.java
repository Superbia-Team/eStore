package com.dm.estore.search.config.solr.server;

import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.server.SolrServerFactory;
import org.springframework.data.solr.server.support.MulticoreSolrServerFactory;

import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.search.config.solr.SearchServer;


public class StandaloneSearchServer implements SearchServer {
	
	private static final Logger LOG = LoggerFactory.getLogger(StandaloneSearchServer.class);
	
	private MulticoreSolrServerFactory solrServerFactory;
	
	@Override
	public void start(final String homeFolder, Configuration configuration) {

		List<Object> serversList = configuration.getList(CommonConstants.Cfg.Search.CFG_CLOUD_URL);
		if (CollectionUtils.isEmpty(serversList)) throw new RuntimeException("Invalid Solr configuration. At least one server connection is required.");
		
		try {
			SolrServer solrServer = serversList.size() > 1 ? 
					new LBHttpSolrServer(serversList.toArray(new String[] {})) : 
					new HttpSolrServer(String.valueOf(serversList.get(0)));

			solrServerFactory = new MulticoreSolrServerFactory(solrServer);
		} catch (MalformedURLException e) {
			LOG.error("Unable to create Solr connection", e);
			throw new RuntimeException("Unable to create Solr connection", e);
		}
	}

	@Override
	public void shutdown() {
		solrServerFactory.destroy();
		solrServerFactory = null;
	}

	@Override
	public SolrServerFactory getServerFactory() {
		return solrServerFactory;
	}
}
