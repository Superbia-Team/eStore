package com.dm.estore.server;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.util.resource.EmptyResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectsMetaInfConfiguration extends MetaInfConfiguration {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProjectsMetaInfConfiguration.class);
	
	@SuppressWarnings("resource")
	public void scanForResources (WebAppContext context, Resource jar, ConcurrentHashMap<Resource,Resource> cache)
    throws Exception
    {
        Resource resourcesDir = null;
        if (cache != null && cache.containsKey(jar))
        {
            resourcesDir = cache.get(jar);  
            if (resourcesDir == EmptyResource.INSTANCE)
            {
                if (LOG.isDebugEnabled()) LOG.debug(jar+" cached as containing no META-INF/resources");
                return;    
            }
            else
                if (LOG.isDebugEnabled()) LOG.debug(jar+" META-INF/resources found in cache ");
        }
        else
        {
            //not using caches or not in the cache so check for the resources dir
            if (LOG.isDebugEnabled()) LOG.debug(jar+" META-INF/resources checked");
            URI uri = jar.getURI();
            resourcesDir = jar.isDirectory() ? Resource.newResource(uri + "META-INF/resources") : Resource.newResource("jar:"+uri+"!/META-INF/resources");
            if (!resourcesDir.exists() || !resourcesDir.isDirectory())
                resourcesDir = EmptyResource.INSTANCE;

            if (cache != null)
            {               
                Resource old  = cache.putIfAbsent(jar, resourcesDir);
                if (old != null)
                    resourcesDir = old;
                else
                    if (LOG.isDebugEnabled()) LOG.debug(jar+" META-INF/resources cache updated");
            }

            if (resourcesDir == EmptyResource.INSTANCE)
                return;
        }

        //add it to the meta inf resources for this context
        @SuppressWarnings("unchecked")
		Set<Resource> dirs = (Set<Resource>)context.getAttribute(METAINF_RESOURCES);
        if (dirs == null)
        {
            dirs = new HashSet<Resource>();
            context.setAttribute(METAINF_RESOURCES, dirs);
        }
        if (LOG.isDebugEnabled()) LOG.debug(resourcesDir+" added to context");
        dirs.add(resourcesDir);
    }
    
    /**
     * Scan for META-INF/web-fragment.xml file in the given jar.
     * 
     * @param context
     * @param jar
     * @param cache
     * @throws Exception
     */
    @SuppressWarnings("resource")
	public void scanForFragment (WebAppContext context, Resource jar, ConcurrentHashMap<Resource,Resource> cache)
    throws Exception
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
            //not using caches or not in the cache so check for the web-fragment.xml
            if (LOG.isDebugEnabled()) LOG.debug(jar+" META-INF/web-fragment.xml checked");
            URI uri = jar.getURI();
            webFrag = jar.isDirectory() ? Resource.newResource(uri + "META-INF/web-fragment.xml") : Resource.newResource("jar:"+uri+"!/META-INF/web-fragment.xml");
            if (!webFrag.exists() || webFrag.isDirectory())
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
		Map<Resource, Resource> fragments = (Map<Resource,Resource>)context.getAttribute(METAINF_FRAGMENTS);
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
    public void scanForTlds (WebAppContext context, Resource jar, ConcurrentHashMap<Resource, Collection<URL>> cache)
    throws Exception
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
            Resource metaInfDir = null;
            try {
	            metaInfDir = jar.isDirectory() ? Resource.newResource(uri + "META-INF/") : Resource.newResource("jar:"+uri+"!/META-INF/");
	
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
            } finally {
            	if (metaInfDir != null) {
            		metaInfDir.close();
            	}
            }
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
