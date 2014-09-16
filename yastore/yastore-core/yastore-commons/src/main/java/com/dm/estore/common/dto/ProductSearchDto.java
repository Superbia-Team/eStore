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
package com.dm.estore.common.dto;

import java.io.Serializable;

public class ProductSearchDto implements Serializable {

    private static final long serialVersionUID = -5236153013027643649L;
    
    private final String searchTerms;
    private final int page;
    private final int pageSize;
    private final String category;
    private final String[] materials;
    private final String[] colors;
    private final String sortColumn;
    private final String sortOrder;
    
    private ProductSearchDto(Builder builder) {
        this.searchTerms = builder.searchTerms;
        this.page = builder.page;
        this.pageSize = builder.pageSize;
        this.category = builder.category;
        this.materials = builder.materials;
        this.colors = builder.colors;
        this.sortColumn = builder.sortColumn;
        this.sortOrder = builder.sortOrder;
    }

    public String getSearchTerms() {
        return searchTerms;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getCategory() {
        return category;
    }

    public String[] getMaterials() {
        return materials;
    }

    public String[] getColors() {
        return colors;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public String getSortOrder() {
        return sortOrder;
    }
    
    public static class Builder {
        private String searchTerms;
        private int page;
        private int pageSize;
        private String category;
        private String[] materials;
        private String[] colors;
        private String sortColumn;
        private String sortOrder;
        
        public Builder searchTerms(String searchTerms) {
            this.searchTerms = searchTerms;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder materials(String[] materials) {
            this.materials = materials;
            return this;
        }

        public Builder colors(String[] colors) {
            this.colors = colors;
            return this;
        }

        public Builder sortColumn(String sortColumn) {
            this.sortColumn = sortColumn;
            return this;
        }

        public Builder sortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public ProductSearchDto build() {
            return new ProductSearchDto(this);
        }
    }
}
