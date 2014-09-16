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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductSearchResultDto {
    
    private List<ProductDto> products;
    private long totalCount;
    
    private Map<String, Long> categoriesFacet = new HashMap<String, Long>();
    private Map<String, Long> groupFacet = new HashMap<String, Long>();
    private Map<String, Long> subGroupFacet = new HashMap<String, Long>();

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public Map<String, Long> getCategoriesFacet() {
        return categoriesFacet;
    }

    public void setCategoriesFacet(Map<String, Long> categoriesFacet) {
        this.categoriesFacet = categoriesFacet;
    }

    public Map<String, Long> getGroupFacet() {
        return groupFacet;
    }

    public void setGroupFacet(Map<String, Long> groupFacet) {
        this.groupFacet = groupFacet;
    }

    public Map<String, Long> getSubGroupFacet() {
        return subGroupFacet;
    }

    public void setSubGroupFacet(Map<String, Long> subGroupFacet) {
        this.subGroupFacet = subGroupFacet;
    }
}
