package com.dm.estore.search.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.dm.estore.common.converter.AbstractModelToDtoConverter;
import com.dm.estore.core.models.Category;
import com.dm.estore.core.models.I18NStringValue;
import com.dm.estore.core.models.Product;
import com.dm.estore.core.models.Tag;
import com.dm.estore.search.dto.CatalogItemDto;
import com.dm.estore.search.dto.CatalogItemType;

@Component(value = "productModelToCatalogDtoConverter")
public class ProductModelToCatalogDtoConverter extends AbstractModelToDtoConverter<Product, CatalogItemDto> {
	
	private static final String ENG_LANG = new Locale("en").getLanguage();
	
	protected CatalogItemDto createTarget() {
		return new CatalogItemDto(CatalogItemType.product);
	}

	@Override
	protected void convertInternal(final Product source, final CatalogItemDto target) {
		target.setDocumentId(source.getId());
		target.setCode(source.getSku());

		target.setActive(source.isActive());
		target.setInStock(source.getInventory() > 0);
		target.setPopularity(source.getPopularity());
		target.setImage(source.getImage());
		target.setListImage(source.getListingImage());
		target.setImages(source.getImages());
		
		if (source.getPrice() != null) {
			target.setPrice(source.getPrice().getAmount().floatValue());
		}

		if (!CollectionUtils.isEmpty(source.getTags())) {
			List<String> tags = new ArrayList<String>();
			for (Tag tag : source.getTags()) {
				tags.add(tag.getCode());
			}
		}
		
		// Name
		Map<String, String> localizedNames = new HashMap<String, String>();
		Map<Locale, I18NStringValue> names = source.getNames();
		for (Map.Entry<Locale, I18NStringValue> entry : names.entrySet()) {
			final String lang = entry.getKey().getLanguage(); 
			if (!lang.equals(ENG_LANG)) {
				localizedNames.put(lang, entry.getValue().getValue());
			} else {
				// the default fields in Solr configuration are for English
				target.setName(entry.getValue().getValue());
			}
		}
		target.setNames(localizedNames);
		
		// Summary
		Map<String, String> localizedSummaries = new HashMap<String, String>();
		Map<Locale, I18NStringValue> summaries = source.getSummaries();
		for (Map.Entry<Locale, I18NStringValue> entry : summaries.entrySet()) {
			final String lang = entry.getKey().getLanguage(); 
			if (!lang.equals(ENG_LANG)) {
				localizedSummaries.put(lang, entry.getValue().getValue());
			} else {
				// the default fields in Solr configuration are for English
				target.setSummary(entry.getValue().getValue());
			}
		}
		target.setSummaries(localizedSummaries);
		
		// Description
		Map<String, String> localizedDescriptions = new HashMap<String, String>();
		Map<Locale, I18NStringValue> descriptions = source.getDescriptions();
		for (Map.Entry<Locale, I18NStringValue> entry : descriptions.entrySet()) {
			final String lang = entry.getKey().getLanguage(); 
			if (!lang.equals(ENG_LANG)) {
				localizedDescriptions.put(lang, entry.getValue().getValue());
			} else {
				// the default fields in Solr configuration are for English
				target.setDescription(entry.getValue().getValue());
			}
		}
		target.setDescriptions(localizedDescriptions);
		
		if (!CollectionUtils.isEmpty(source.getCategories())) {
			List<String> categories = new ArrayList<String>();
			for (Category category : source.getCategories()) {
			    combineCategories(category, categories);
			}
			target.setCategories(categories);
		}
		
		target.setVariantGroup(source.getVariantGroup());
		target.setVariantSubGroup(source.getVariantSubGroup());
	}
	
	private void combineCategories(Category category, List<String> categories) {
	    categories.add(category.getCode());
	    
	    Category parent = category.getParent();
        while (null != parent) {
            categories.add(parent.getCode());
            parent = parent.getParent();
        }
	}
}
