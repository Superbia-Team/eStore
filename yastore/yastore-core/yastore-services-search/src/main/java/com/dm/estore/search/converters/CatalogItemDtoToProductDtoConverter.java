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
import com.dm.estore.common.dto.ProductDto;
import com.dm.estore.search.dto.CatalogItemDto;
import com.dm.estore.search.dto.CatalogItemType;

@Component(value = "catalogItemDtoToProductDtoConverter")
public class CatalogItemDtoToProductDtoConverter  extends AbstractModelToDtoConverter<CatalogItemDto, ProductDto> {
    
    @Override
    protected ProductDto createTarget() {
        return new ProductDto();
    }
    
    @Override
    protected void convertInternal(final CatalogItemDto source, final ProductDto target) {
        super.convertInternal(source, target);
        
        if (!CatalogItemType.product.equals(source.getDocumentType())) {
            throw new ConvertionException("Invalid catalog item type for conversion.");
        }
        
//      Long documentId;
//      String code;
//      String name;
//      String summary;
//      String description;
//      boolean inStock;
//      int popularity;
//      float price;
//      List<String> categories;
//      List<String> tags;
//      Map<String, String> names = new HashMap<String, String>();
//      Map<String, String>  summaries = new HashMap<String, String>();
//      Map<String, String>  descriptions = new HashMap<String, String>();
//      boolean active;
//      List<String> images;
//      String image;
//      String listImage;        
    }
}
