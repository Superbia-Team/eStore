package com.dm.estore.search.config.solr.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.server.SolrServerFactory;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactory;
import org.xml.sax.SAXException;

import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.common.exceptions.SearchServerConnectionException;
import com.dm.estore.common.utils.FileUtils;
import com.dm.estore.search.config.solr.SearchServer;

public class EmbeddedSearchServer implements SearchServer {

	private static final String SOLR_CFG = "solr.xml";
	private static final String SOLR_HOME = "solr";
	private static final String SOLR_DATA = "data";
	
	private static final Logger LOG = LoggerFactory.getLogger(EmbeddedSearchServer.class);

	private EmbeddedSolrServerFactory solrServerFactory;
	private final boolean unitTests;
	
	private static final String[] SOLR_CORES = {
		"solr/catalog/",
		"solr/documents/"
	};

	private static final String[] CORE_FILES = {
		"core.properties",
		
		"conf/admin-extra.html",
		"conf/admin-extra.menu-bottom.html",
		"conf/admin-extra.menu-top.html",
		"conf/currency.xml",
		"conf/elevate.xml",
		"conf/mapping-FoldToASCII.txt",
		"conf/mapping-ISOLatin1Accent.txt",
		"conf/protwords.txt",
		"conf/_schema_analysis_stopwords_english.json",
		"conf/_schema_analysis_synonyms_english.json",
		"conf/schema.xml",
		"conf/scripts.conf",
		"conf/solrconfig.xml",
		"conf/spellings.txt",
		"conf/stopwords.txt",
		"conf/synonyms.txt",
		"conf/update-script.js",

		"conf/clustering/carrot2/kmeans-attributes.xml",
		"conf/clustering/carrot2/lingo-attributes.xml",
		"conf/clustering/carrot2/stc-attributes.xml",

		"conf/xslt/example_atom.xsl",
		"conf/xslt/example_rss.xsl",
		"conf/xslt/example.xsl",
		"conf/xslt/luke.xsl",
		"conf/xslt/updateXml.xsl",

		"conf/lang/contractions_ca.txt",
		"conf/lang/contractions_fr.txt",
		"conf/lang/contractions_ga.txt",
		"conf/lang/contractions_it.txt",
		"conf/lang/hyphenations_ga.txt",
		"conf/lang/stemdict_nl.txt",
		"conf/lang/stoptags_ja.txt",
		"conf/lang/stopwords_ar.txt",
		"conf/lang/stopwords_bg.txt",
		"conf/lang/stopwords_ca.txt",
		"conf/lang/stopwords_ckb.txt",
		"conf/lang/stopwords_cz.txt",
		"conf/lang/stopwords_da.txt",
		"conf/lang/stopwords_de.txt",
		"conf/lang/stopwords_el.txt",
		"conf/lang/stopwords_en.txt",
		"conf/lang/stopwords_es.txt",
		"conf/lang/stopwords_eu.txt",
		"conf/lang/stopwords_fa.txt",
		"conf/lang/stopwords_fi.txt",
		"conf/lang/stopwords_fr.txt",
		"conf/lang/stopwords_ga.txt",
		"conf/lang/stopwords_gl.txt",
		"conf/lang/stopwords_hi.txt",
		"conf/lang/stopwords_hu.txt",
		"conf/lang/stopwords_hy.txt",
		"conf/lang/stopwords_id.txt",
		"conf/lang/stopwords_it.txt",
		"conf/lang/stopwords_ja.txt",
		"conf/lang/stopwords_lv.txt",
		"conf/lang/stopwords_nl.txt",
		"conf/lang/stopwords_no.txt",
		"conf/lang/stopwords_pt.txt",
		"conf/lang/stopwords_ro.txt",
		"conf/lang/stopwords_ru.txt",
		"conf/lang/stopwords_sv.txt",
		"conf/lang/stopwords_th.txt",
		"conf/lang/stopwords_tr.txt",
		"conf/lang/userdict_ja.txt",

		"conf/velocity/browse.vm",
		"conf/velocity/cluster_results.vm",
		"conf/velocity/cluster.vm",
		"conf/velocity/debug.vm",
		"conf/velocity/did_you_mean.vm",
		"conf/velocity/error.vm",
		"conf/velocity/facet_fields.vm",
		"conf/velocity/facet_pivot.vm",
		"conf/velocity/facet_queries.vm",
		"conf/velocity/facet_ranges.vm",
		"conf/velocity/facets.vm",
		"conf/velocity/footer.vm",
		"conf/velocity/header.vm",
		"conf/velocity/head.vm",
		"conf/velocity/hit_grouped.vm",
		"conf/velocity/hit_plain.vm",
		"conf/velocity/hit.vm",
		"conf/velocity/join_doc.vm",
		"conf/velocity/jquery.autocomplete.css",
		"conf/velocity/jquery.autocomplete.js",
		"conf/velocity/layout.vm",
		"conf/velocity/main.css",
		"conf/velocity/mime_type_lists.vm",
		"conf/velocity/pagination_bottom.vm",
		"conf/velocity/pagination_top.vm",
		"conf/velocity/product_doc.vm",
		"conf/velocity/query_form.vm",
		"conf/velocity/query_group.vm",
		"conf/velocity/query_spatial.vm",
		"conf/velocity/query.vm",
		"conf/velocity/results_list.vm",
		"conf/velocity/richtext_doc.vm",
		"conf/velocity/suggest.vm",
		"conf/velocity/tabs.vm",
		"conf/velocity/VM_global_library.vm"
	};
	
	private static final String[] COMMON_FILES = {
		"solr/solr.xml",
		"solr/zoo.cfg",
		"solr/libs/README.txt"
	};

	public EmbeddedSearchServer(final boolean unitTests) {
		this.unitTests = unitTests;
	}

	@Override
	public void start(final String homeFolder, Configuration configuration) {
		
		final String solrHomePath = homeFolder + File.separator + SOLR_HOME + File.separator + SOLR_CFG;
		File solrHome = new File(solrHomePath);
		if (unitTests || !solrHome.exists()) {
			
			FileUtils.createFilesFromClasspath(homeFolder, 
					Arrays.asList(COMMON_FILES),
	        		Arrays.asList(new String[]{}));
			
			for (String core : SOLR_CORES) {
				FileUtils.createFilesFromClasspath(homeFolder, 
						coreFilesList(core),
		        		Arrays.asList(new String[]{}));
			}
			
			if (unitTests) {
				final File dataFolder = new File(homeFolder + CommonConstants.App.CFG_APP_NAME + File.separator + "data");
				// If indexing a brand-new index, you might want to delete the data directory first
				if (dataFolder.exists()) {
					try {
						FileUtils.deleteDirectory(dataFolder);
					} catch (IOException e) {
						LOG.warn("Unable to clean up Solr data folder", e);
					}
				}
			}
		}

		final String solrHomeFolder = homeFolder + File.separator + SOLR_HOME;
		final String solrDataFolder = solrHomeFolder + File.separator + SOLR_DATA;
    	System.setProperty("solr.data.dir", solrDataFolder);

    	try {
    		FileUtils.forceMkdir(new File(solrDataFolder + File.separator + "documents"));
    		FileUtils.forceMkdir(new File(solrDataFolder + File.separator + "catalog"));
    		
    		solrServerFactory = new MulticoreEmbeddedSolrServerFactory(solrHomeFolder);
			LOG.debug("Server created. Cores: ");
			for (String coreName : solrServerFactory.getCores()) {
				LOG.debug("  " + coreName);
			}
		} catch (ParserConfigurationException | IOException | SAXException e) {
			LOG.error("Unable to create embedded Solr server", e);
			throw new SearchServerConnectionException("Unable to create embedded Solr server", e);
		}
	}
	
	private List<String> coreFilesList(final String coreName) {
		List<String> files = new ArrayList<String>();
		
		for (String coreFile : CORE_FILES) {
			files.add(coreName + coreFile);
		}
		
		return files;
	}

	@Override
	public void shutdown() {
		// Shutdown embedded server
		if (solrServerFactory != null) {
			solrServerFactory.shutdownSolrServer();
			solrServerFactory = null;
		}
	}

	@Override
	public SolrServerFactory getServerFactory() {
		return solrServerFactory;
	}
}
