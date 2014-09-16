package com.dm.estore.search.config.solr.server;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactory;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

public class MulticoreEmbeddedSolrServerFactory  extends EmbeddedSolrServerFactory {
	
	private Map<String, SolrServer> serverMap = new LinkedHashMap<String, SolrServer>();
	
	public MulticoreEmbeddedSolrServerFactory(String solrHome) throws ParserConfigurationException, IOException, SAXException {
		super(solrHome);
	}
	
	@Override
	public SolrServer getSolrServer(String coreName) {
		if (!StringUtils.hasText(coreName)) {
			return getSolrServer();
		}

		if (!serverMap.containsKey(coreName)) {
			serverMap.put(coreName, createServerForCore(coreName));
		}
		return serverMap.get(coreName);
	}
	
	private SolrServer createServerForCore(final String coreName) {
		return new EmbeddedSolrServer(getSolrServer().getCoreContainer(), coreName);
	}
}
