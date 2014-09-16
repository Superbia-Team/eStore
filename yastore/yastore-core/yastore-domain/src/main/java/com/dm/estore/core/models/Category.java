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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;

import com.dm.estore.core.repository.listeners.ModelChangeListener;

/**
 * A product.
 * 
 * @author dmorozov
 */
@Entity
@Table(name = "CATEGORIES")
@EntityListeners({ModelChangeListener.class})
public class Category extends AbstractEntity {

	private static final long serialVersionUID = 1469128998049547153L;
	
	public static final String ROOT_CATEGORY = "ROOT";	
	
	@Column(name="CODE", nullable = false, unique = true)
	private String code;

	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER, optional = false)
	@JoinColumn(name="NAME_ID")
	private I18NString name = new I18NString();

	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY, optional = true)
	@JoinColumn(name="SUMMARY_ID")
	private I18NString summary = new I18NString();

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "CATEGORY_ATTRIBUTES", joinColumns = {@JoinColumn(name="CATEGORY_ID")})
	private Map<String, String> attributes = new HashMap<String, String>();
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinTable(
		name="CATEGORY_TAGS",
		joinColumns = @JoinColumn( name="CATEGORY_ID"), inverseJoinColumns = @JoinColumn( name="TAG_ID")
	)
    private List<Tag> tags;
	
	@Column(name="IS_HOME")
	private boolean homeCategory;
	
	@Column(name="IMAGE")
	private String image;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT")
    private Category parent;
	
	@Column(name="DISPLAY_ORDER", nullable = true)
    private int displayOrder;
	
	@Column(name="URL", nullable = true)
    private String url;

	/**
	 * Creates a new {@link Category} with the given name.
	 * 
	 * @param name must not be {@literal null} or empty.
	 * @param price must not be {@literal null} or less than or equal to zero.
	 */
	public Category(String code, String name) {
		this(code, name, null);
	}

	/**
	 * Creates a new {@link Category} from the given name and description.
	 * 
	 * @param name must not be {@literal null} or empty.
	 * @param price must not be {@literal null} or less than or equal to zero.
	 * @param description
	 */
	public Category(String code, String name, String summary) {

		Assert.hasText(name, "Name must not be null or empty!");

		this.code = code;
		if (name != null) this.name.addString(LocaleContextHolder.getLocale(), name);
		if (summary != null) this.summary.addString(LocaleContextHolder.getLocale(), summary);
	}

	protected Category() {

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
	 * Returns all the custom attributes of the {@link Category}.
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
		this.name.addString(LocaleContextHolder.getLocale(), name);
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
		this.summary.addString(LocaleContextHolder.getLocale(), name);
	}
	
	public Map<Locale, I18NStringValue> getSummaries() {
        return this.summary.getAll();
    }
	// ======================================
	
    public boolean isHomeCategory() {
        return homeCategory;
    }

    public void setHomeCategory(boolean homeCategory) {
        this.homeCategory = homeCategory;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
