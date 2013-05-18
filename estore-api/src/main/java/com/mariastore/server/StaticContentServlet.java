package com.mariastore.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticContentServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(StaticContentServlet.class);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String contextPath = request.getContextPath();
		String resourceRelativePath = request.getRequestURI().toString();
		if (resourceRelativePath.startsWith(contextPath)) {
			resourceRelativePath = resourceRelativePath.substring(contextPath.length());
		}
		processStaticResources(resourceRelativePath, response);
	}

	protected boolean processStaticResources(String resourceRelativePath, HttpServletResponse response) {
		// TODO: implement cache for static resources for fast resource
		// availability check
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