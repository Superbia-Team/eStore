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
package com.dm.estore.core.config.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;

import au.com.bytecode.opencsv.CSVReader;

import com.dm.estore.common.constants.SecurityConstants;
import com.dm.estore.common.exceptions.ConfigurationException;
import com.dm.estore.common.utils.OrderUtils;
import com.dm.estore.core.config.InitDataLoader;
import com.dm.estore.core.models.Category;
import com.dm.estore.core.models.Product;
import com.dm.estore.core.models.Tag;
import com.dm.estore.core.models.User;
import com.dm.estore.core.repository.CategoryRepository;
import com.dm.estore.core.repository.ProductRepository;
import com.dm.estore.core.repository.TagRepository;
import com.dm.estore.core.repository.UserRepository;

/**
 * Initial DB data loader
 * 
 * @author dmorozov
 */
public class InitDataLoaderImpl implements InitDataLoader {
	
	private static final Logger LOG = LoggerFactory.getLogger(InitDataLoaderImpl.class);
	
	private static final int BATCH_SIZE = 20;
	
	private static final String HEADER_MARKER = "#SKU";
	private static final String HEADER_MARKER2 = "Category Code";
	private static final String LIST_SEPARATOR = ",";
	private static final Locale LOCALE_RU = new Locale( "ru", "RU" );
	
	private final Map<String, Category> categoriesCache = new HashMap<String, Category>();
	private final Map<String, Product> productsCache = new HashMap<String, Product>();
	private final Map<String, Tag> tagsCache = new HashMap<String, Tag>();
	
	@SuppressWarnings("serial")
	private static final List<String> BOOLEAN_TRUE_CONST = new ArrayList<String>(){{
		add("active");
		add("true");
		add("1");
		add("yes");
	}};
	
	@Autowired
	private ProductRepository productsRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TagRepository tagRepository;
	
	public interface ParseHandler {
		void handle(String[] row);
	}

	@Override
	@Transactional
	public void loadData() {
		
		// check if DB already initialized
		User systemUser = userRepository.findByUserName(SecurityConstants.USER_NAME_SYSTEM);
		if (systemUser != null) return;
		
		// Create system user
		systemUser = new User(SecurityConstants.USER_NAME_SYSTEM);
		userRepository.save(systemUser);
				
		loadCategories();
		loadProducts();
	}
	
	private void loadCategories() {
		try {
			final List<Category> categories = new ArrayList<Category>();
			readCategories(new ParseHandler() {
				@Override
				public void handle(String[] row) {
					final Category category = parseCategory(row);
					if (category != null) {
						categories.add(category);
						categoriesCache.put(category.getCode(), category);
						if (categories.size() > BATCH_SIZE) {
							categoryRepository.save(categories);
							categories.clear();
						}
					}
				}
			});
			if (categories.size() > 0) {
				categoryRepository.save(categories);
				categories.clear();
			}
			
			LOG.info("Total imported products count: {}", productsRepository.count());
			
		} catch (Exception e) {
			LOG.error("Unable to import products", e);
			throw new ConfigurationException("Unable to import products", e);
		}
	}
	
	private void loadProducts() {
		try {
			final List<Product> products = new ArrayList<Product>();
			readProducts(new ParseHandler() {
				@Override
				public void handle(String[] row) {
					final Product product = parseProduct(row);
					if (product != null) {
						products.add(product);
						if (products.size() > BATCH_SIZE) {
							productsRepository.save(products);
							products.clear();
						}
					}
				}
			});
			if (products.size() > 0) {
				productsRepository.save(products);
				products.clear();
			}
			
			LOG.info("Total imported products count: {}", productsRepository.count());
			
		} catch (Exception e) {
			LOG.error("Unable to import products", e);
			throw new ConfigurationException("Unable to import products", e);
		}		
	}
	
	private void readProducts(final ParseHandler handler) throws IOException {
		Enumeration<URL> resources = ClassLoader.getSystemResources("Products.csv");
		System.out.print("products: " + resources.hasMoreElements());
		
		try (
				InputStream is = ClassLoader.getSystemResourceAsStream("Products.csv");
				final InputStreamReader fileInputStream = new InputStreamReader(is);
				final CSVReader reader = new CSVReader(fileInputStream);
			) {
			
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				handler.handle(nextLine);
			}
		}
	}
	
	private void readCategories(final ParseHandler handler) throws IOException {
		try (
				InputStream is = ClassLoader.getSystemResourceAsStream("Categories.csv");
				final InputStreamReader fileInputStream = new InputStreamReader(is);
				final CSVReader reader = new CSVReader(fileInputStream);
			) {
			
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				handler.handle(nextLine);
			}
		}
	}
	
	private Product parseProduct(final String[] row) {
		// SKU, Status, Name (EN), Tax Code, Price, MSRP, Summary (EN), Description (EN), Tags, Categories, Country Catalog, Best Seller, New, Inventory, Weight, Shippable, Base product, Variant Group 1, Variant Group 2, Listing image, Image, All Images
		if (row != null && !StringUtils.isEmpty(row[0]) && !HEADER_MARKER.equalsIgnoreCase(row[0]) && row.length >= 18) {
			
			final Product product = new Product(row[0], row[2], null);
			product.setActive(parseSafeBoolean(row[1]));
			product.setTaxCode(row[3]);
			product.setPrice(parseSafeMoney(row[4], row[10]));
			product.setMsrp(parseSafeMoney(row[5], row[10]));			
			product.setSummary(Locale.US, row[6]);
			product.setSummary(LOCALE_RU, row[6]); // this is just for testing
			product.setDescription(row[7]);
			
			String[] tags = StringUtils.isEmpty(row[8]) ? new String[0] : row[8].split(LIST_SEPARATOR);
			List<Tag> productTags = new ArrayList<Tag>();
			for (String code : tags) {
				Tag tag = tagsCache.containsKey(code) ? tagsCache.get(code) : new Tag(code);
				if (tag.getId() == null) {
					tag = tagRepository.save(tag);
				}
				tagsCache.put(code, tag);
				productTags.add(tag);
			}
			if (productTags.size() > 0) {
				product.setTags(productTags);
			}

			List<Category> categories = new ArrayList<Category>();
			String[] categoryCodes = StringUtils.isEmpty(row[9]) ? new String[0] : row[9].split(LIST_SEPARATOR);
			for (String code : categoryCodes) {
				Category category = categoriesCache.containsKey(code) ? categoriesCache.get(code) : null;
				if (category == null) {
					LOG.warn("Category with code '" + code + "' not found for a product '" + product.getSku() + "'!");
				} else if (categories.contains(category)) {
					LOG.warn("Category with code '" + code + "' added twice for a product '" + product.getSku() + "'!");
				} else {
					categories.add(category);
				}
			}
			product.setCategories(categories);
			
			final String country = StringUtils.isEmpty(row[10]) ? LocaleContextHolder.getLocale().getCountry() : row[10];
			product.setCountry(country);
			
			product.setBestSeller(parseSafeBoolean(row[11]));
			product.setNewProduct(parseSafeBoolean(row[12]));
			product.setInventory(parseSafeInteger(row[13], 0));
			product.setWeight(parseSafeInteger(row[14], 0));
			product.setShippable(parseSafeBoolean(row[15]));
			
			if (!StringUtils.isEmpty(row[16])) {
				Product baseProduct = productsCache.get(row[16]);
				if (baseProduct != null) {
					product.setBaseProduct(baseProduct);
				}
			}
			
			product.setVariantGroup(row[17]);
			product.setVariantSubGroup(row[18]);
			
			product.setListingImage(row[19]);
			product.setImage(row[20]);
			if (!StringUtils.isEmpty(row[21])) {
			    product.setImages(Arrays.asList(row[21].split(LIST_SEPARATOR)));
			}
			
			productsCache.put(product.getSku(), product);
			return product;
		}
		
		return null;
	}
	
	private Category parseCategory(final String[] row) {
	    // Category Code, Parent, Category Name (EN), Category Summary (EN), Tags, Show on Home, Image, Display Order, URL
		if (row != null && !StringUtils.isEmpty(row[0]) && !HEADER_MARKER2.equalsIgnoreCase(row[0]) && row.length >= 4 ) {
			
			final Category category = new Category(row[0], row[2], row[3]);
			if (!StringUtils.isEmpty(row[1]) && categoriesCache.containsKey(row[1])) {
			    category.setParent(categoriesCache.get(row[1]));
			}
			
            String[] tags = StringUtils.isEmpty(row[4]) ? new String[0] : row[4].split(LIST_SEPARATOR);
            List<Tag> productTags = new ArrayList<Tag>();
            for (String code : tags) {
                Tag tag = tagsCache.containsKey(code) ? tagsCache.get(code) : new Tag(code);
                if (tag.getId() == null) {
                    tag = tagRepository.save(tag);
                }
                tagsCache.put(code, tag);
                productTags.add(tag);
            }
            if (productTags.size() > 0) {
                category.setTags(productTags);
            }
            
            category.setHomeCategory(parseSafeBoolean(row[5]));
            category.setImage(row[6]);
            category.setDisplayOrder(parseSafeInteger(row[7], 999));
            category.setUrl(row[8]);
			return category;
		}
		
		return null;
	}

	private BigDecimal parseSafeDouble(final String value) {
		if (!StringUtils.isEmpty(value)) {
			try {
				return BigDecimal.valueOf(Double.parseDouble(value));
			} catch (Exception e) {
				// do nothing
			}
		}
		
		return BigDecimal.ZERO;
	}
	
	private int parseSafeInteger(final String value, int defaultValue) {
		if (!StringUtils.isEmpty(value)) {
			return StringUtils.isNumeric(value) ? Integer.parseInt(value) : defaultValue;
		}
		
		return defaultValue;
	}
	
	private Money parseSafeMoney(final String value, final String countryCode) {
		final BigDecimal amount = parseSafeDouble(value);
		final CurrencyUnit currency = StringUtils.isEmpty(countryCode) ? 
				CurrencyUnit.of(LocaleContextHolder.getLocale()) 
				: CurrencyUnit.of(OrderUtils.resolveCurrencyByCountryCode(countryCode));
		return Money.of(currency, amount);
	}
	
	private boolean parseSafeBoolean(final String value) {
		if (!StringUtils.isEmpty(value)) {
			if (BOOLEAN_TRUE_CONST.contains(value.toLowerCase())) {
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}
	
}
