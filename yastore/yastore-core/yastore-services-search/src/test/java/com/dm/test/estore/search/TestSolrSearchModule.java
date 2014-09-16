package com.dm.test.estore.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.SolrServerFactory;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.xml.sax.SAXException;

import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.common.exceptions.SearchServerConnectionException;
import com.dm.estore.common.utils.FileUtils;
import com.dm.estore.search.config.solr.CustomSolrRepositoryFactoryBean;
import com.dm.estore.search.config.solr.server.MulticoreEmbeddedSolrServerFactory;
import com.dm.estore.search.dto.DocumentDto;
import com.dm.estore.search.dto.CatalogItemDto;
import com.dm.estore.search.repository.DocumentsRepository;
import com.dm.estore.search.repository.CatalogSearchRepository;

@EnableTransactionManagement
@ComponentScan(basePackages = {
	    "com.dm.estore.search.converters",
	    "com.dm.estore.search.repository",
	    "com.dm.estore.search.services"
	})
@EnableSolrRepositories(
	basePackages = {
		"com.dm.estore.search.repository"
	}
	, multicoreSupport = true
	, repositoryFactoryBeanClass = CustomSolrRepositoryFactoryBean.class
)
public class TestSolrSearchModule {
	
	private static final Logger LOG = LoggerFactory.getLogger(TestSolrSearchModule.class);
	
	private EmbeddedSolrServerFactory solrServerFactory;
	
    @PostConstruct
    public void initialize() {
        LOG.info("Initialize '" + CommonConstants.App.CFG_APP_NAME + "' Solr search services");

        try {
			initializeSolrServer();
		} catch (IOException e) {
			throw new SearchServerConnectionException("Unable to initialize embedded Solr instance", e);
		}
    }
    
    @PreDestroy
    public void shutdown() {
    	solrServerFactory.shutdownSolrServer();
    }
    
    private void initializeSolrServer() throws IOException {
    	final String solrHomeFolder = Paths.get("").toAbsolutePath().toString() + File.separator + "build"+ File.separator + "test-base";
    	final String solrDataFolder = solrHomeFolder + File.separator + "data";
    	System.setProperty("solr.data.dir", solrDataFolder);
    	
    	FileUtils.forceMkdir(new File(solrDataFolder + File.separator + "documents"));
    	FileUtils.forceMkdir(new File(solrDataFolder + File.separator + "catalog"));
    	
    	try {
    		solrServerFactory = new MulticoreEmbeddedSolrServerFactory("classpath:solr");
			LOG.debug("Server created. Cores: ");
			for (String coreName : solrServerFactory.getCores()) {
				LOG.debug("  " + coreName);
			}
		} catch (ParserConfigurationException | IOException | SAXException e) {
			LOG.error("Unable to create embedded Solr server", e);
			throw new SearchServerConnectionException("Unable to create embedded Solr server", e);
		}
    }
    
    @Bean
    public SolrServerFactory getSolrServerFactory() {
    	return solrServerFactory;
    }
    
    @Bean(name = "solrServer")
    public SolrServer getSolrServer() {
    	return solrServerFactory.getSolrServer();
    }
    
    @Bean(name = DocumentsRepository.TEMPLATE_CONTEXT_NAME)
    public SolrTemplate documentsTemplate(SolrServerFactory solrServerFactory) throws Exception {
      SolrTemplate solrTemplate = new SolrTemplate(solrServerFactory.getSolrServer(DocumentDto.CORE_NAME));
      solrTemplate.setSolrCore(DocumentDto.CORE_NAME);
      return solrTemplate;
    }

    @Bean(name = CatalogSearchRepository.TEMPLATE_CONTEXT_NAME)
    public SolrTemplate productsTemplate(SolrServerFactory solrServerFactory) throws Exception {
      SolrTemplate solrTemplate = new SolrTemplate(solrServerFactory.getSolrServer(CatalogItemDto.CORE_NAME));
      solrTemplate.setSolrCore(CatalogItemDto.CORE_NAME);
      return solrTemplate;
    }
}
