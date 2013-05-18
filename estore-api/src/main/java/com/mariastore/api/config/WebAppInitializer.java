package com.mariastore.api.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;

import org.springframework.web.*;
import org.springframework.web.context.*;
import org.springframework.web.context.support.*;
import org.springframework.web.servlet.*;

import com.mariastore.server.StaticContentServlet;

public class WebAppInitializer implements WebApplicationInitializer
{
	private static final String DISPATCHER_SERVLET_NAME = "dispatcher";
	private static final String STATIC_SERVLET_NAME = "static";
	private static final Map<String, String> contexts = new HashMap<String, String>(){{
		put(DISPATCHER_SERVLET_NAME, "/");
		put(STATIC_SERVLET_NAME, "/site/*");
	}};
	
	@Override
	public void onStartup(ServletContext aServletContext) throws ServletException
	{		
		registerListener(aServletContext);
		registerDispatcherServlet(aServletContext);
		registerJspServlet(aServletContext);
	}
	
	private void registerListener(ServletContext aContext)
	{
		AnnotationConfigWebApplicationContext _root = createContext(ApplicationModule.class);
		aContext.addListener(new ContextLoaderListener(_root));
	}
	
	private void registerDispatcherServlet(ServletContext aContext)
	{
		AnnotationConfigWebApplicationContext _ctx = createContext(WebModule.class);
		ServletRegistration.Dynamic _dispatcher = 
			aContext.addServlet(DISPATCHER_SERVLET_NAME, new DispatcherServlet(_ctx));
		_dispatcher.setLoadOnStartup(1);
		_dispatcher.addMapping(contexts.get(DISPATCHER_SERVLET_NAME));
	}
	
	private AnnotationConfigWebApplicationContext createContext(final Class<?>... aModules)
	{
		AnnotationConfigWebApplicationContext _ctx = new AnnotationConfigWebApplicationContext();
		_ctx.register(aModules);
		return _ctx;
	}
	
	private void registerJspServlet(ServletContext aContext) {
		ServletRegistration.Dynamic _dispatcher = 
			aContext.addServlet(STATIC_SERVLET_NAME, new StaticContentServlet());
		_dispatcher.setLoadOnStartup(1);
		_dispatcher.addMapping(contexts.get(STATIC_SERVLET_NAME));
	}
}
