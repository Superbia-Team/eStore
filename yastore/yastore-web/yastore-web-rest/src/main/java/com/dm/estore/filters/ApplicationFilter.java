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

package com.dm.estore.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author dmorozov
 */
public class ApplicationFilter implements Filter {

    private static final String HTML_EXT = ".html";
    private static final String SETUP_PAGE = "setup" + HTML_EXT;
    private static final String DEFAULT_PAGE = "index" + HTML_EXT;
    private static final String DEFAULT_CONTEX = "/";

    public ApplicationFilter() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = request instanceof HttpServletRequest ? (HttpServletRequest) request : null;
        HttpServletResponse httpResponse = (HttpServletResponse)response;

        final String resourceRelativePath = httpRequest != null ? httpRequest.getRequestURI() : "";

        if (DEFAULT_CONTEX.equals(resourceRelativePath)) {
            httpResponse.sendRedirect(DEFAULT_PAGE);
        } else if (systemInitialized()
                || resourceRelativePath.endsWith(SETUP_PAGE)
                || !resourceRelativePath.endsWith(HTML_EXT)) {
            filterChain.doFilter(request, response);
        } else {
            if (response instanceof HttpServletResponse) {
                ((HttpServletResponse)response).sendRedirect(SETUP_PAGE);
            }
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    private boolean systemInitialized() {
        // Not implemented
        return true;
    }
}
