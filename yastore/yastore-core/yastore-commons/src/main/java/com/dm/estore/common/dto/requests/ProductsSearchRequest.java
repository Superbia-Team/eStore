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
package com.dm.estore.common.dto.requests;

import com.dm.estore.common.dto.ProductSearchDto;


/**
 * Search for products request
 * 
 * @author dmorozov
 */
public class ProductsSearchRequest extends EmptyRequest {

    private static final long serialVersionUID = -5768863604911179416L;

    private final ProductSearchDto request;
    
    public ProductsSearchRequest(final ProductSearchDto request) {
        this.request = request;
    }

    public ProductSearchDto getRequest() {
        return request;
    }
}
