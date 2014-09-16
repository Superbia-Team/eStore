/*
 * Copyright 2014 Denis Morozov.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dm.estore.search.config;

import static com.dm.estore.core.services.config.akka.AkkaSpringExtension.SpringExtProvider;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.SolrServerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.dm.estore.common.akka.ServicesConstants;
import com.dm.estore.common.config.Cfg;
import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.common.dto.requests.system.CreateChildRequest;
import com.dm.estore.common.utils.ActorUtils;
import com.dm.estore.search.akka.SearchIndexingActor;
import com.dm.estore.search.config.solr.CustomSolrRepositoryFactoryBean;
import com.dm.estore.search.config.solr.SearchServer;
import com.dm.estore.search.config.solr.server.EmbeddedSearchServer;
import com.dm.estore.search.config.solr.server.StandaloneSearchServer;
import com.dm.estore.search.dto.CatalogItemDto;
import com.dm.estore.search.dto.DocumentDto;
import com.dm.estore.search.repository.CatalogSearchRepository;
import com.dm.estore.search.repository.DocumentsRepository;

/**
 *
 * @author dmorozov
 */
@Configuration
@ComponentScan(basePackages = {
    "com.dm.estore.search"
})
@EnableSolrRepositories(
	basePackages = {
		"com.dm.estore.search.repository"
	}
	, multicoreSupport = true
    , repositoryFactoryBeanClass = CustomSolrRepositoryFactoryBean.class
)
public class SolrSearchModule {
	
    private static final Logger LOG = LoggerFactory.getLogger(SolrSearchModule.class);
    
    @Resource(name = "actorSystem")
    private ActorSystem actorSystem;
    
    @Resource
    private Cfg configuration;
    
    private SearchServer searchServer;
    
    @PostConstruct
    public void initialize() {
        LOG.info("Initialize '" + CommonConstants.App.CFG_APP_NAME + "' Solr search services");

        initializeSolrServer();
        initializeActors();
    }
    
    @PreDestroy
    public void shutdown() {
        if (searchServer != null) {
        	searchServer.shutdown();
        }
        searchServer = null;
    }
    
    private void initializeSolrServer() {
    	String serverType = configuration.getConfig().getString(CommonConstants.Cfg.Search.CFG_SERVER_TYPE);
    	if (SearchServer.ServerType.embedded.name().equalsIgnoreCase(serverType)) {
    		searchServer = new EmbeddedSearchServer(false);
    	} else {
    		searchServer = new StandaloneSearchServer();
    	}
    	
    	searchServer.start(configuration.getHomeFolder(), configuration.getConfig());
    }
    
    private void initializeActors() {
    	// ask parent to create child
    	ActorSelection sel = actorSystem.actorSelection(ActorUtils.ROOT_CONTEXT_PATH + ServicesConstants.SERVICES_ROOT_PATH);
    	
    	Props actorProps2 = SpringExtProvider.get(this.actorSystem).props(SearchIndexingActor.CONTEXT_NAME);
    	sel.tell(new CreateChildRequest(actorProps2, SearchIndexingActor.ACTOR_NAME, SearchIndexingActor.ACTOR_NAME, false), ActorRef.noSender());
    }
    
    @Bean
    public SolrServerFactory getSolrServerFactory() {
    	return searchServer.getServerFactory();
    }
    
    @Bean(name = "solrServer")
    public SolrServer getSolrServer() {
    	return searchServer.getServerFactory().getSolrServer();
    }
    
    @Bean(name = DocumentsRepository.TEMPLATE_CONTEXT_NAME)
    public SolrTemplate documentsTemplate(SolrServerFactory solrServerFactory) throws Exception {
      SolrTemplate solrTemplate = new SolrTemplate(solrServerFactory.getSolrServer(DocumentDto.CORE_NAME));
      solrTemplate.setSolrCore(DocumentDto.CORE_NAME);
      return solrTemplate;
    }

    @Bean(name = CatalogSearchRepository.TEMPLATE_CONTEXT_NAME)
    public SolrTemplate catalogTemplate(SolrServerFactory solrServerFactory) throws Exception {
      SolrTemplate solrTemplate = new SolrTemplate(solrServerFactory.getSolrServer(CatalogItemDto.CORE_NAME));
      solrTemplate.setSolrCore(CatalogItemDto.CORE_NAME);
      return solrTemplate;
    }
}
