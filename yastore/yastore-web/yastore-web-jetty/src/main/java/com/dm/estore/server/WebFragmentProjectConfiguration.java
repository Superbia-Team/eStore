package com.dm.estore.server;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.util.resource.EmptyResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebFragmentProjectConfiguration extends MetaInfConfiguration {
	
	private static final Logger LOG = LoggerFactory.getLogger(WebFragmentProjectConfiguration.class);
	
	private static final String WEB_FRAGMENT_DESCRIPTOR = "META-INF/web-fragment.xml";
	
	private static final String ECLIPSE_PROJECT_PATH_PATTERN = "build/classes/eclipse";
	
	@Override
    public void preConfigure(final WebAppContext context) throws Exception
    {        
        boolean useContainerCache = DEFAULT_USE_CONTAINER_METAINF_CACHE;
        Boolean attr = (Boolean)context.getServer().getAttribute(USE_CONTAINER_METAINF_CACHE);
        if (attr != null)
            useContainerCache = attr.booleanValue();
        
        if (LOG.isDebugEnabled()) LOG.debug("{} = {}", USE_CONTAINER_METAINF_CACHE, useContainerCache);
        
        List<Resource> projects = loadResources(WEB_FRAGMENT_DESCRIPTOR, null);
        scanJars(context, projects, useContainerCache);
    }
	
	public List<Resource> loadResources(final String name, final ClassLoader classLoader) throws IOException {
	    final List<Resource> list = new ArrayList<Resource>();
	    
	    final Enumeration<URL> systemResources = (classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader).getResources(name);
	    while (systemResources.hasMoreElements()) {
	    	URL webFragment = systemResources.nextElement();
	    	String path = webFragment.getPath();
	    	if (path.contains(ECLIPSE_PROJECT_PATH_PATTERN)) {
	    		path = path.substring(0, path.indexOf(ECLIPSE_PROJECT_PATH_PATTERN) + ECLIPSE_PROJECT_PATH_PATTERN.length());
	    		list.add(Resource.newResource(path));
	    	}
	    }
	    return list;
	}
	
	/**
     * Scan for META-INF/web-fragment.xml file in the given jar.
     * 
     * @param context
     * @param jar
     * @param cache
     * @throws Exception
     */
	@Override
    public void scanForFragment (WebAppContext context, Resource jar, ConcurrentHashMap<Resource,Resource> cache) throws Exception
    {
        Resource webFrag = null;
        if (cache != null && cache.containsKey(jar))
        {
            webFrag = cache.get(jar);  
            if (webFrag == EmptyResource.INSTANCE)
            {
                if (LOG.isDebugEnabled()) LOG.debug(jar+" cached as containing no META-INF/web-fragment.xml");
                return;     
            }
            else
                if (LOG.isDebugEnabled()) LOG.debug(jar+" META-INF/web-fragment.xml found in cache ");
        }
        else
        {
            webFrag = Resource.newResource(jar.getURI() + "META-INF/web-fragment.xml");
            if (!webFrag.exists())
                webFrag = EmptyResource.INSTANCE;
            
            if (cache != null)
            {
                //web-fragment.xml doesn't exist: put token in cache to signal we've seen the jar               
                Resource old = cache.putIfAbsent(jar, webFrag);
                if (old != null)
                    webFrag = old;
                else
                    if (LOG.isDebugEnabled()) LOG.debug(jar+" META-INF/web-fragment.xml cache updated");
            }
            
            if (webFrag == EmptyResource.INSTANCE)
                return;
        }

        @SuppressWarnings("unchecked")
		Map<Resource, Resource> fragments = (Map<Resource,Resource>) context.getAttribute(METAINF_FRAGMENTS);
        if (fragments == null)
        {
            fragments = new HashMap<Resource, Resource>();
            context.setAttribute(METAINF_FRAGMENTS, fragments);
        }
        fragments.put(jar, webFrag);   
        if (LOG.isDebugEnabled()) LOG.debug(webFrag+" added to context");
    }
	
	/**
     * Discover META-INF/*.tld files in the given jar
     * 
     * @param context
     * @param jar
     * @param cache
     * @throws Exception
     */
	@Override
    public void scanForTlds (WebAppContext context, Resource jar, ConcurrentHashMap<Resource, Collection<URL>> cache) throws Exception
    {
        Collection<URL> tlds = null;
        
        if (cache != null && cache.containsKey(jar))
        {
            Collection<URL> tmp = cache.get(jar);
            if (tmp.isEmpty())
            {
                if (LOG.isDebugEnabled()) LOG.debug(jar+" cached as containing no tlds");
                return;
            }
            else
            {
                tlds = tmp;
                if (LOG.isDebugEnabled()) LOG.debug(jar+" tlds found in cache ");
            }
        }
        else
        {
            //not using caches or not in the cache so find all tlds
            URI uri = jar.getURI();
            Resource metaInfDir = Resource.newResource(uri + "META-INF/");

            //find any *.tld files inside META-INF or subdirs
            tlds = new HashSet<URL>();      
            Collection<Resource> resources = metaInfDir.getAllResources();
            for (Resource t:resources)
            {
                String name = t.toString();
                if (name.endsWith(".tld"))
                {
                    if (LOG.isDebugEnabled()) LOG.debug(t+" tld discovered");
                    tlds.add(t.getURL());
                }
            }
            if (cache != null)
            {  
                if (LOG.isDebugEnabled()) LOG.debug(jar+" tld cache updated");
                Collection<URL> old = cache.putIfAbsent(jar, tlds);
                if (old != null)
                    tlds = old;
            }
            
            if (tlds.isEmpty())
                return;
        }

        @SuppressWarnings("unchecked")
		Collection<URL> tld_resources = (Collection<URL>) context.getAttribute(METAINF_TLDS);
        if (tld_resources == null)
        {
            tld_resources = new HashSet<URL>();
            context.setAttribute(METAINF_TLDS, tld_resources);
        }
        tld_resources.addAll(tlds);  
        if (LOG.isDebugEnabled()) LOG.debug("tlds added to context");
    }
}
