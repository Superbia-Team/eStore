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
package com.dm.estore.core.models;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;

import com.dm.estore.core.repository.listeners.ModelChangeListener;

/**
 * A product.
 * 
 * @author dmorozov
 */
@Entity
@Table(name = "PRODUCTS")
@EntityListeners({ModelChangeListener.class})
public class Product extends AbstractEntity {

	private static final long serialVersionUID = 1469128998049547153L;
	
	@Column(name="SKU", nullable = false, unique = true)
	private String sku;
	
	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER, optional = true)
	@JoinColumn(name="NAME_ID")
	private I18NString name = new I18NString();

	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY, optional = true)
	@JoinColumn(name="SUMMARY_ID")
	private I18NString summary = new I18NString();
	
	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY, optional = true)
	@JoinColumn(name="DESCRIPTION_ID")
	private I18NString description = new I18NString();
	
	@Type(type="com.dm.estore.core.config.usertype.MoneyUserType")
	@Columns(columns = {@Column(name = "PRICE"), @Column(name = "PRICE_CURRENCY")})
	private Money price;
	
	@Type(type="com.dm.estore.core.config.usertype.MoneyUserType")
	@Columns(columns = {@Column(name = "MSRP"), @Column(name = "MSRP_CURRENCY")})
	private Money msrp;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "PRODUCT_ATTRIBUTES", joinColumns = {@JoinColumn(name="PRODUCT_ID")})
	private Map<String, String> attributes = new HashMap<String, String>();
	
	@ManyToMany
	@JoinTable(
		name="PRODUCT_TAGS",
		joinColumns = @JoinColumn( name="PRODUCT_ID"), inverseJoinColumns = @JoinColumn( name="TAG_ID")
	)
    List<Tag> tags;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name="PRODUCT_CATEGORIES",
		joinColumns = @JoinColumn( name="PRODUCT_ID"), inverseJoinColumns = @JoinColumn( name="CATEGORY_ID")
	)
    List<Category> categories;
	
	@Column(name = "ACTIVE")
	private boolean active = Boolean.TRUE;
	
	@Column(name = "TAXCODE")
	private String taxCode;

	@Column(name = "COUNTRY")
	private String country;
	
	@Column(name = "BEST_SELLER")
	private boolean bestSeller;
	
	@Column(name = "NEW_PRODUCT")
	private boolean newProduct;
	
	@Column(name = "INVENTORY")
	private int inventory;
	
	@Column(name = "WEIGHT")
	private int weight;
	
	@Column(name = "SHIPPABLE")
	private boolean shippable;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BASE_PRODUCT")
	private Product baseProduct;

	@Column(name = "VAR_GROUP")
	private String variantGroup;
	
	@Column(name = "VAR_SUB_GROUP")
	private String variantSubGroup;
	
	@Column(name = "POPULARITY")
	private int popularity;
	
	@Column(name = "IMAGE")
	private String image;
	
	@Column(name = "LIST_IMAGE")
	private String listingImage;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "PRODUCT_IMAGES", joinColumns = @JoinColumn(name="PRODUCT_ID"))
	private List<String> images;
	

	/**
	 * Creates a new {@link Product} with the given name.
	 * 
	 * @param name must not be {@literal null} or empty.
	 * @param price must not be {@literal null} or less than or equal to zero.
	 */
	public Product(String sku, String name, BigDecimal price) {
		this(sku, name, price, null);
	}

	/**
	 * Creates a new {@link Product} from the given name and description.
	 * 
	 * @param name must not be {@literal null} or empty.
	 * @param price must not be {@literal null} or less than or equal to zero.
	 * @param description
	 */
	public Product(String sku, String name, BigDecimal price, String description) {

		Assert.hasText(name, "Name must not be null or empty!");

		this.sku = sku;
		if (price != null) this.price = Money.of(CurrencyUnit.getInstance(LocaleContextHolder.getLocale()), price);
		if (name != null) this.name.addString(LocaleContextHolder.getLocale(), name);
		if (description != null) this.description.addString(LocaleContextHolder.getLocale(), description);
	}

	public Product() {

	}

	/**
	 * Sets the attribute with the given name to the given value.
	 * 
	 * @param name must not be {@literal null} or empty.
	 * @param value
	 */
	public void setAttribute(String name, String value) {

		Assert.hasText(name);

		if (value == null) {
			this.attributes.remove(value);
		} else {
			this.attributes.put(name, value);
		}
	}

	/**
	 * Returns all the custom attributes of the {@link Product}.
	 * 
	 * @return
	 */
	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	// ======================================
	public String getName() {
		return name.getString(LocaleContextHolder.getLocale());
	}
	
	public void setName(String name) {
		this.name.addString(LocaleContextHolder.getLocale(), name);
	}
	
	public String getName(Locale locale) {
		return name.getString(locale);
	}
	
	public void setName(Locale locale, String name) {
		this.name.addString(locale, name);
	}
	
	public Map<Locale, I18NStringValue> getNames() {
		return this.name.getAll();
	}
	// ======================================

	// ======================================
	public String getSummary() {
		return summary.getString(LocaleContextHolder.getLocale());
	}
	
	public void setSummary(String name) {
		this.summary.addString(LocaleContextHolder.getLocale(), name);
	}
	
	public String getSummary(Locale locale) {
		return summary.getString(locale);
	}
	
	public void setSummary(Locale locale, String name) {
		this.summary.addString(locale, name);
	}
	
	public Map<Locale, I18NStringValue> getSummaries() {
		return this.summary.getAll();
	}
	// ======================================
	
	// ======================================
	public String getDescription() {
		return description.getString(LocaleContextHolder.getLocale());
	}
	
	public void setDescription(String description) {
		this.description.addString(LocaleContextHolder.getLocale(), description);
	}
	
	public String getDescription(Locale locale) {
		return description.getString(locale);
	}
	
	public void setDescription(Locale locale, String description) {
		this.description.addString(locale, description);
	}
	
	public Map<Locale, I18NStringValue> getDescriptions() {
		return this.description.getAll();
	}
	// ======================================
	
	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public Money getPrice() {
		return price;
	}

	public void setPrice(Money price) {
		this.price = price;
	}

	public Money getMsrp() {
		return msrp;
	}

	public void setMsrp(Money msrp) {
		this.msrp = msrp;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean isBestSeller() {
		return bestSeller;
	}

	public void setBestSeller(boolean bestSeller) {
		this.bestSeller = bestSeller;
	}

	public boolean isNewProduct() {
		return newProduct;
	}

	public void setNewProduct(boolean newProduct) {
		this.newProduct = newProduct;
	}

	public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public boolean isShippable() {
		return shippable;
	}

	public void setShippable(boolean shippable) {
		this.shippable = shippable;
	}

	public Product getBaseProduct() {
		return baseProduct;
	}

	public void setBaseProduct(Product baseProduct) {
		this.baseProduct = baseProduct;
	}

	public String getVariantGroup() {
		return variantGroup;
	}

	public void setVariantGroup(String variantGroup) {
		this.variantGroup = variantGroup;
	}

	public String getVariantSubGroup() {
		return variantSubGroup;
	}

	public void setVariantSubGroup(String variantSubGroup) {
		this.variantSubGroup = variantSubGroup;
	}

	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getListingImage() {
        return listingImage;
    }

    public void setListingImage(String listingImage) {
        this.listingImage = listingImage;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
