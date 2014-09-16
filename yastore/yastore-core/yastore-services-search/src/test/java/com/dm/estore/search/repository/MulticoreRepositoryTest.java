package com.dm.estore.search.repository;

import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.dm.estore.search.dto.DocumentDto;
import com.dm.estore.search.dto.CatalogItemDto;
import com.dm.test.estore.search.TestSolrSearchModule;

@Test
@ContextConfiguration(classes = {TestSolrSearchModule.class})
public class MulticoreRepositoryTest extends AbstractTestNGSpringContextTests {
	
	@Resource
	private CatalogSearchRepository productsRepository;
	
	@Resource
	private DocumentsRepository documentsRepository;
	
	/**
     * The annotated method will be run before any test method belonging to the classes
     * inside the <test> tag is run.
     *
     */
    @BeforeTest
    public void beforeTest() {
        logger.debug("BeforeTest method is run...");
    }
 
    /**
     * The annotated method will be run before the first test method in the current class
     * is invoked.
     *
     */
    @BeforeClass
    public void beforeClass() {
        logger.debug("BeforeClass method is run...");
    }
 
    /**
     * The annotated method will be run before each test method.
     *
     */
    @BeforeMethod
    public void beforeMethod() {
        logger.debug("BeforeMethod method is run...");
        
        productsRepository.deleteAll();
        documentsRepository.deleteAll();
    }
    
    private CatalogItemDto createTestProduct() {
    	final String uuid = UUID.randomUUID().toString();
    	
    	CatalogItemDto product = new CatalogItemDto();
    	product.setDocumentId(Long.valueOf(uuid.hashCode()));
    	product.setName("testProduct" + uuid);
    	product.setDescription("test product description " + uuid);
    	
    	return product;
    }
    
    private DocumentDto createTestDocument() {
    	final String uuid = UUID.randomUUID().toString();
    	
    	DocumentDto doc = new DocumentDto();
    	doc.setId(uuid);
    	doc.setName("testProduct" + uuid);
    	doc.setDescription("test product description " + uuid);
    	
    	return doc;
    }    
 
    /**
     * Tests multicore Solr usage
     *
     */
    @Test
    public void addProduct() {
    	
    	CatalogItemDto product = createTestProduct();
    	CatalogItemDto  savedProduct = productsRepository.save(product);
    	Assert.assertNotNull(savedProduct);
    	Assert.assertEquals(savedProduct.getName(), product.getName());

    	Iterable<CatalogItemDto> iter = productsRepository.findAll();
    	Assert.assertNotNull(iter);
    	Assert.assertNotNull(iter.iterator());
    	Assert.assertTrue(iter.iterator().hasNext());
    	
    	CatalogItemDto searchResult = iter.iterator().next();
    	Assert.assertNotNull(searchResult);
    	Assert.assertEquals(searchResult.getName(), product.getName());
    	
    	Iterable<DocumentDto> documentsIter = documentsRepository.findAll();
    	Assert.assertNotNull(documentsIter);
    	Assert.assertNotNull(documentsIter.iterator());
    	Assert.assertFalse(documentsIter.iterator().hasNext());
    }
    
    /**
     * Tests multicore Solr usage
     *
     */
    @Test
    public void addDocument() {
    	
    	DocumentDto product = createTestDocument();
    	DocumentDto  savedProduct = documentsRepository.save(product);
    	Assert.assertNotNull(savedProduct);
    	Assert.assertEquals(savedProduct.getName(), product.getName());

    	Iterable<DocumentDto> iter = documentsRepository.findAll();
    	Assert.assertNotNull(iter);
    	Assert.assertNotNull(iter.iterator());
    	Assert.assertTrue(iter.iterator().hasNext());
    	
    	DocumentDto searchResult = iter.iterator().next();
    	Assert.assertNotNull(searchResult);
    	Assert.assertEquals(searchResult.getName(), product.getName());
    	
    	Iterable<CatalogItemDto> documentsIter = productsRepository.findAll();
    	Assert.assertNotNull(documentsIter);
    	Assert.assertNotNull(documentsIter.iterator());
    	Assert.assertFalse(documentsIter.iterator().hasNext());
    }
}
