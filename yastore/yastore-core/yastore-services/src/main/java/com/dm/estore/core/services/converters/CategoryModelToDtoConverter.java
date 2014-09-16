package com.dm.estore.core.services.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.dm.estore.common.converter.AbstractModelToDtoConverter;
import com.dm.estore.common.dto.CategoryDto;
import com.dm.estore.common.dto.CategoryFullDto;
import com.dm.estore.core.models.Category;
import com.dm.estore.core.models.I18NStringValue;
import com.dm.estore.core.models.Tag;

@Component(value = "categoryModelToDtoConverter")
public class CategoryModelToDtoConverter extends AbstractModelToDtoConverter<Category, CategoryDto> {
	
	private static final String ENG_LANG = new Locale("en").getLanguage();
	
	protected CategoryDto createTarget() {
		return new CategoryDto();
	}

	@Override
	protected void convertInternal(final Category source, final CategoryDto target) {
		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setSummary(source.getSummary());
		target.setImage(source.getImage());
		
		if (target instanceof CategoryFullDto) {
		    
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
	        ((CategoryFullDto) target).setNames(localizedNames);
	        
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
	        ((CategoryFullDto) target).setSummaries(localizedSummaries);
	        
	        if (!CollectionUtils.isEmpty(source.getTags())) {
	            List<String> tags = new ArrayList<String>();
	            for (Tag tag : source.getTags()) {
	                tags.add(tag.getCode());
	            }
	            ((CategoryFullDto) target).setTags(tags);
	        }
		}
	}
}
