package com.dm.estore.core.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.startsWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableWithSize;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.dm.estore.core.models.I18NString;
import com.dm.estore.core.models.I18NStringValue;
import com.dm.estore.core.models.Product;
import com.dm.test.estore.core.TestPersistenceModule;

@Test
@ContextConfiguration(classes = {TestPersistenceModule.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProductRepositoryTest extends AbstractTestNGSpringContextTests {
	
	@Autowired
	private ProductRepository productsRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	private Product generateTestProduct() {
		final String uuid = UUID.randomUUID().toString(); 
		Product product = new Product();
		product.setSku(uuid);
		product.setPrice(Money.of(CurrencyUnit.USD, BigDecimal.ONE));
		product.setName(Locale.US, "This is name for US " + uuid);
		product.setName(Locale.CANADA, "This is name for CANADA " + uuid);
		product.setSummary(Locale.US, "This is summary for US: " + uuid);
		product.setSummary(Locale.CANADA, "This is summary for CANADA: " + uuid);
		product.setDescription("This is looong description");
		return product;
	}
	
	@Test
    public void addProducts() {
		List<Product> products = new ArrayList<Product>();
		products.add(generateTestProduct());
		products.add(generateTestProduct());
		products.add(generateTestProduct());
		productsRepository.save(products);
		
		List<Product> productsFromDB = productsRepository.findAll();
		assertThat(productsFromDB.size(), greaterThanOrEqualTo(3));

		assertThat(productsFromDB.get(0), allOf(
			hasProperty("names",
				hasEntry(equalTo(LocaleContextHolder.getLocale()), hasProperty("value", startsWith("This is name for")))					
    		),
			hasProperty("descriptions",
				hasValue(hasProperty("value", Matchers.is("This is looong description")))
    		),
    		hasProperty("summaries", allOf(
	            hasKey(Locale.US),
	            hasKey(Locale.CANADA)
    		))
        ));
	}
	
	@Test
    public void addProductI18N() {
		Product product = new Product("TESTSKU", "This is test product", BigDecimal.ONE);
		product.setSummary(Locale.US, "This is summary for US");
		product.setSummary(Locale.CANADA, "This is summary for CANADA");
		Product productUpdated = productsRepository.save(product);
		
		Product productFromDB = productsRepository.findOne(productUpdated.getId());
		Assert.assertNotNull(productFromDB);
		Assert.assertEquals(product.getSku(), productFromDB.getSku());
		Assert.assertEquals(product.getName(), productFromDB.getName());
		Assert.assertNotNull(productFromDB.getSummaries());
		
		assertThat(productFromDB.getSummaries().size(), Matchers.is(2));
		assertThat(productFromDB.getSummaries().entrySet(), everyItem(isIn(product.getSummaries().entrySet())));
	}
	
	@Test
    public void getProductI18N() {
		Product product = generateTestProduct();
		Product productUpdated = productsRepository.save(product);
		
		Product productFromDB = em.find(Product.class, productUpdated.getId());
		Assert.assertNotNull(productFromDB);
		Assert.assertEquals(product.getSku(), productFromDB.getSku());
		Assert.assertEquals(product.getName(), productFromDB.getName());
		
		// We have to have valid count of names. I.e. the same as we saved
		assertThat(productFromDB.getNames().size(), Matchers.is(product.getNames().size()));
		assertThat(productFromDB.getNames().entrySet(), everyItem(isIn(product.getNames().entrySet())));
	}
	
	@Test
    public void productNameCascadeDelete() {

		// delete all localized strings
		Query deleteQuery = em.createQuery("delete from " + I18NString.class.getName());
		deleteQuery.executeUpdate();
		
		Product product = generateTestProduct();
		Product productUpdated = productsRepository.save(product);
		
		TypedQuery<I18NString> selectQuery = em.createQuery("select o from " + I18NString.class.getName() + " o", I18NString.class);
		List<I18NString> allLocalizedStrings = selectQuery.getResultList();
		// names + summaries + descriptions
		assertThat(allLocalizedStrings, IsIterableWithSize.<I18NString>iterableWithSize(3));
		
		productUpdated.getSummaries().clear();
		productUpdated.getDescriptions().clear();
		productsRepository.save(productUpdated);
		
		allLocalizedStrings = selectQuery.getResultList();
		int totalCount = 0;
		for (I18NString str : allLocalizedStrings) totalCount += str.getAll().size();
		
		// only names
		assertThat(totalCount, Matchers.is(product.getNames().size()));
		
		productUpdated = productsRepository.findOne(productUpdated.getId());
		assertThat(productUpdated.getNames().values(), IsIterableWithSize.<I18NStringValue>iterableWithSize(product.getNames().size()));
		assertThat(productUpdated.getSummaries().values(), emptyCollectionOf(I18NStringValue.class));
		assertThat(productUpdated.getDescriptions().values(), emptyCollectionOf(I18NStringValue.class));
	}
	
	@Test
    public void searchBySku() {
		Product product = generateTestProduct();
		productsRepository.save(product);
		
		Product result = productsRepository.findBySku(product.getSku());
		Assert.assertNotNull(result);
	}
	
	@Test
    public void getLocalizedName() {
		Product product = generateTestProduct();
		product = productsRepository.save(product);
		
		I18NStringValue result = productsRepository.findName(product.getId(), LocaleContextHolder.getLocale());
		Assert.assertNotNull(result);
		Assert.assertEquals(result.getValue(), product.getName());
	}
}
