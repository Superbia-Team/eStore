package com.mariastore.api.config;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import com.mariastore.server.StaticContentServlet;

public class WebAppInitializer implements WebApplicationInitializer
{
	private static final String DISPATCHER_SERVLET_NAME = "dispatcher";
	private static final String SECURITY_FILTER_NAME = "springSecurityFilterChain";
	
	private static final String STATIC_SERVLET_NAME = "static";
	private static final Map<String, String> contexts = new HashMap<String, String>(){{
		put(DISPATCHER_SERVLET_NAME, "/api/*");
		put(STATIC_SERVLET_NAME, "/site/*");
	}};
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException
	{		
		registerListener(servletContext);
		registerDispatcherServlet(servletContext);
		registerStaticContentServlet(servletContext);
	}
	
	private void registerListener(ServletContext servletContext)
	{
		AnnotationConfigWebApplicationContext _root = createContext(ApplicationModule.class);
		servletContext.addListener(new ContextLoaderListener(_root));
	}
	
	private void registerDispatcherServlet(ServletContext servletContext)
	{
		AnnotationConfigWebApplicationContext _ctx = createContext(WebModule.class);
		ServletRegistration.Dynamic _dispatcher = 
			servletContext.addServlet(DISPATCHER_SERVLET_NAME, new DispatcherServlet(_ctx));
		_dispatcher.setLoadOnStartup(1);
		_dispatcher.addMapping(contexts.get(DISPATCHER_SERVLET_NAME));
		
		FilterRegistration.Dynamic _filter = servletContext.addFilter(SECURITY_FILTER_NAME, new DelegatingFilterProxy(SECURITY_FILTER_NAME));
		_filter.setAsyncSupported(true);
		_filter.addMappingForServletNames(
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC), 
				false, DISPATCHER_SERVLET_NAME);
	}
	
	private AnnotationConfigWebApplicationContext createContext(final Class<?>... modules)
	{
		AnnotationConfigWebApplicationContext _ctx = new AnnotationConfigWebApplicationContext();
		_ctx.register(modules);
		return _ctx;
	}
	
	private void registerStaticContentServlet(ServletContext servletContext) {
		ServletRegistration.Dynamic _dispatcher = 
			servletContext.addServlet(STATIC_SERVLET_NAME, new StaticContentServlet());
		_dispatcher.setLoadOnStartup(1);
		_dispatcher.addMapping(contexts.get(STATIC_SERVLET_NAME));
	}
}
