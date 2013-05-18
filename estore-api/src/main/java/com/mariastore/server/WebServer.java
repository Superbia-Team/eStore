package com.mariastore.server;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jetty.annotations.AbstractDiscoverableAnnotationHandler;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.AnnotationDecorator;
import org.eclipse.jetty.annotations.AnnotationParser;
import org.eclipse.jetty.annotations.AnnotationParser.DiscoverableAnnotationHandler;
import org.eclipse.jetty.annotations.ClassNameResolver;
import org.eclipse.jetty.annotations.WebFilterAnnotationHandler;
import org.eclipse.jetty.annotations.WebListenerAnnotationHandler;
import org.eclipse.jetty.annotations.WebServletAnnotationHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class WebServer
{
	private static final Logger LOG = LoggerFactory.getLogger(WebServer.class);

	private static final String WEB_XML = "META-INF/MANIFEST.MF";
	private static final String CLASS_ONLY_AVAILABLE_IN_IDE = "com.mariastore.IDE";
	// For in-IDE mode
	private static final String PROJECT_RELATIVE_PATH_TO_WEBAPP = "src/main/webapp";
	private static final String LOG_PATH_IDE = "target/logs/yyyy_mm_dd.request.log";
	
	private static final int DEFAULT_MAXIMUM_IDLE_TIME = 1000 * 60 * 60;
	private static final int ALLOWED_SHUTDOWN_TIME = 1000 * 5;
	private static final String SERVER_NAME = "eStoreServer";
	
    private Server server;
    private WebServerConfig config;
    
    public WebServer(WebServerConfig aConfig)
    {
        config = aConfig;
    }
    
    public void start() throws Exception
    {
    	LOG.info("Starting server on port: " + config.getPort());
        server = new Server();

        server.setThreadPool(createThreadPool());
        server.addConnector(createConnector());
        server.setHandler(createHandlers());        
        server.setStopAtShutdown(true);
        server.setGracefulShutdown(ALLOWED_SHUTDOWN_TIME);
                
        server.start();
    }
    
    public void join() throws InterruptedException
    {
        server.join();
    }
    
    public void stop() throws Exception
    {        
        server.stop();
    }
    
    private ThreadPool createThreadPool()
    {
        QueuedThreadPool _threadPool = new QueuedThreadPool();
        _threadPool.setName(SERVER_NAME);
        _threadPool.setMinThreads(config.getMinThreads());
        _threadPool.setMaxThreads(config.getMaxThreads());
        return _threadPool;
    }
    
    private SelectChannelConnector createConnector()
    {
        SelectChannelConnector _connector = new SelectChannelConnector();
        _connector.setPort(config.getPort());
        _connector.setHost(config.getHostInterface());
		_connector.setMaxIdleTime(DEFAULT_MAXIMUM_IDLE_TIME);
		_connector.setSoLingerTime(-1);
        return _connector;
    }
    
    private HandlerCollection createHandlers()
    {       	    	
        WebAppContext _ctx = new AliasEnhancedWebAppContext();
        _ctx.setResourceAlias("/WEB-INF/classes/", "/classes/");
        _ctx.setContextPath("/");

		if (isRunningInShadedJar()) {
			_ctx.setWar(getShadedWarUrl());
			_ctx.setBaseResource(new ResourceCollection(new String[] {getShadedWarUrl(), "."}));
			//_ctx.setBaseResource(Resource.newClassPathResource("META-INF/webapp"));
		} else {
			_ctx.setWar(PROJECT_RELATIVE_PATH_TO_WEBAPP);
			_ctx.setBaseResource(new ResourceCollection(new String[] {"./src/main/webapp", "./target/mariastore"}));
		}
		
		_ctx.setConfigurations (new Configuration []
		{
			// This is necessary because Jetty out-of-the-box does not scan
			// the classpath of your project in Eclipse, so it doesn't find
			// your WebAppInitializer.
			new AnnotationConfiguration() 
			{
				@Override
				public void configure(WebAppContext context) throws Exception {
				       boolean metadataComplete = context.getMetaData().isMetaDataComplete();
				       context.addDecorator(new AnnotationDecorator(context));   
				      
				       
				       //Even if metadata is complete, we still need to scan for ServletContainerInitializers - if there are any
				       AnnotationParser parser = null;
				       if (!metadataComplete)
				       {
				           //If metadata isn't complete, if this is a servlet 3 webapp or isConfigDiscovered is true, we need to search for annotations
				           if (context.getServletContext().getEffectiveMajorVersion() >= 3 || context.isConfigurationDiscovered())
				           {
				               _discoverableAnnotationHandlers.add(new WebServletAnnotationHandler(context));
				               _discoverableAnnotationHandlers.add(new WebFilterAnnotationHandler(context));
				               _discoverableAnnotationHandlers.add(new WebListenerAnnotationHandler(context));
				           }
				       }
				       
				       //Regardless of metadata, if there are any ServletContainerInitializers with @HandlesTypes, then we need to scan all the
				       //classes so we can call their onStartup() methods correctly
				       createServletContainerInitializerAnnotationHandlers(context, getNonExcludedInitializers(context));
				       
				       if (!_discoverableAnnotationHandlers.isEmpty() || _classInheritanceHandler != null || !_containerInitializerAnnotationHandlers.isEmpty())
				       {           
				           parser = createAnnotationParser();
				           
				           parse(context, parser);
				           
				           for (DiscoverableAnnotationHandler h:_discoverableAnnotationHandlers)
				               context.getMetaData().addDiscoveredAnnotations(((AbstractDiscoverableAnnotationHandler)h).getAnnotationList());      
				       }

				}
				
				private void parse(final WebAppContext context, AnnotationParser parser) throws Exception
				{					
					List<Resource> _resources = getResources(getClass().getClassLoader());
					
					for (Resource _resource : _resources)
					{
						if (_resource == null)
							return;
		            
						parser.clearHandlers();
		                for (DiscoverableAnnotationHandler h:_discoverableAnnotationHandlers)
		                {
		                    if (h instanceof AbstractDiscoverableAnnotationHandler)
		                        ((AbstractDiscoverableAnnotationHandler)h).setResource(null); //
		                }
		                parser.registerHandlers(_discoverableAnnotationHandlers);
		                parser.registerHandler(_classInheritanceHandler);
		                parser.registerHandlers(_containerInitializerAnnotationHandlers);
		                
		                parser.parse(_resource, 
		                             new ClassNameResolver()
		                {
		                    public boolean isExcluded (String name)
		                    {
		                        if (context.isSystemClass(name)) return true;
		                        if (context.isServerClass(name)) return false;
		                        return false;
		                    }
		
		                    public boolean shouldOverride (String name)
		                    {
		                        //looking at webapp classpath, found already-parsed class of same name - did it come from system or duplicate in webapp?
		                        if (context.isParentLoaderPriority())
		                            return false;
		                        return true;
		                    }
		                });
		            }
				}

				private List<Resource> getResources(ClassLoader aLoader) throws IOException
				{
					if (aLoader instanceof URLClassLoader)
		            {
						List<Resource> _result = new ArrayList<Resource>();
		                URL[] _urls = ((URLClassLoader)aLoader).getURLs();		                
		                for (URL _url : _urls)
		                	_result.add(Resource.newResource(_url));
		
		                return _result;
		            }
					return Collections.emptyList();					
				}
			}
		});
        
        List<Handler> _handlers = new ArrayList<Handler>();
        
        _handlers.add(_ctx);
        
        HandlerList _contexts = new HandlerList();
        _contexts.setHandlers(_handlers.toArray(new Handler[0]));
        
        RequestLogHandler _log = new RequestLogHandler();
        _log.setRequestLog(createRequestLog());
        
        HandlerCollection _result = new HandlerCollection();
        _result.setHandlers(new Handler[] {_contexts, _log});
        
        return _result;
    }
    
    /**
	 * Create Jetty request log handler implementation.
	 * Note: we can implement it through SLF4J as well to have all logs in one place.
	 * 
	 * @return Jetty request log handler implementation
	 */
	private RequestLog createRequestLog() {
		NCSARequestLog log = new NCSARequestLog();
		
		File _logPath = new File(!isRunningInShadedJar() ? LOG_PATH_IDE : config.getLogPath());
		_logPath.getParentFile().mkdirs();

		log.setFilename(_logPath.getPath());
		log.setRetainDays(14);
		log.setExtended(false);
		log.setAppend(true);
		log.setLogTimeZone("GMT");
		log.setLogLatency(true);
		return log;
	}
	
	private boolean isRunningInShadedJar() {
		try {
			Class.forName(CLASS_ONLY_AVAILABLE_IN_IDE);
			return false;
		} catch (ClassNotFoundException anExc) {
			return true;
		}
	}

	private URL getResource(String aResource) {
		return Thread.currentThread().getContextClassLoader()
				.getResource(aResource);
	}

	private String getShadedWarUrl() {
		String _urlStr = getResource(WEB_XML).toString();
		return _urlStr.substring(0, _urlStr.length() - WEB_XML.length());
	}
}