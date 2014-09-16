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

package com.dm.estore.server.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author dmorozov
 */
public class StaticContentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(StaticContentServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String resourceRelativePath = request.getRequestURI().toString();
        String contextPath = request.getContextPath();
        if (contextPath != null && resourceRelativePath.indexOf(contextPath) == 0) {
        	resourceRelativePath = resourceRelativePath.substring(contextPath.length());
        }
        
        LOG.trace("Static resource: " + resourceRelativePath);
        processStaticResources(resourceRelativePath, response);
    }

    protected boolean processStaticResources(String resourceRelativePath,
                                             HttpServletResponse response) {

        boolean resourceExist = false;

        ServletOutputStream output = null;
        try {
            String mimeType = getServletContext().getMimeType(resourceRelativePath);
            URL url = getServletContext().getResource(resourceRelativePath);
            if (url != null) {
                resourceExist = true;
                response.setContentType(mimeType);

                URLConnection urlConnect = url.openConnection();
                byte[] buffer = new byte[8 * 1024];
                InputStream input = urlConnect.getInputStream();
                try {
                    output = response.getOutputStream();
                    try {
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                    } finally {
                        output.close();
                        output = null;
                    }
                } finally {
                    input.close();
                    input = null;
                }
            }
        } catch (IOException ioe) {
            LOG.warn("Unable to find resource file: " + resourceRelativePath);
        } finally {
            if (output != null) {
                try {
                    output.flush();
                    output.close();
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }

        return resourceExist;
    }
}