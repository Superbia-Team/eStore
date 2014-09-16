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
package com.dm.estore.search.converters;

import org.springframework.stereotype.Component;

import com.dm.estore.common.converter.AbstractModelToDtoConverter;
import com.dm.estore.common.exceptions.ConvertionException;
import com.dm.estore.common.dto.CategoryDto;
import com.dm.estore.search.dto.CatalogItemDto;
import com.dm.estore.search.dto.CatalogItemType;

@Component(value = "catalogItemDtoToCategoryDtoConverter")
public class CatalogItemDtoToCategoryDtoConverter extends AbstractModelToDtoConverter<CatalogItemDto, CategoryDto> {
    
    @Override
    protected CategoryDto createTarget() {
        return new CategoryDto();
    }
    
    @Override
    protected void convertInternal(final CatalogItemDto source, final CategoryDto target) {
        super.convertInternal(source, target);
        
        if (!CatalogItemType.category.equals(source.getDocumentType())) {
            throw new ConvertionException("Invalid catalog item type for conversion.");
        }
        
        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setSummary(source.getSummary());
        target.setImage(source.getImage());
        
//        Long documentId;
//        int popularity;
//        List<String> categories;
//        List<String> tags;
//        Map<String, String> names = new HashMap<String, String>();
//        Map<String, String>  summaries = new HashMap<String, String>();
//        Map<String, String>  descriptions = new HashMap<String, String>();
//        boolean active;
    }
}
