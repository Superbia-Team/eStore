package com.mariastore.server;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
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
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mariastore.core.utils.SBTJarUtils;
import com.mariastore.core.utils.SBTJarUtils.JarInfo;

public class WebServer {
	private static final Logger LOG = LoggerFactory.getLogger(WebServer.class);

	private static final String WEB_XML = "META-INF/MANIFEST.MF";
	private static final String CLASS_ONLY_AVAILABLE_IN_IDE = "com.mariastore.IDE";
	// For in-IDE mode
	private static final String PROJECT_RELATIVE_PATH_TO_WEBAPP = "src/main/webapp";
	private static final String HOME_PATH_IDE = "target/";
	private static final String LOG_NAME = "logs/mariastore_http_yyyy_mm_dd.log";

	private static final int DEFAULT_MAXIMUM_IDLE_TIME = 30000;
	private static final int ALLOWED_SHUTDOWN_TIME = 1000 * 5;
	private static final String SERVER_NAME = "eStoreServer";
	
	private static final List<String> extensionJars = new ArrayList<String>(){{
		add("estore-api");
		add("estore-extention");
	}}; 
	
	private Server server;
	private WebServerConfig config;

	public WebServer(WebServerConfig aConfig) {
		config = aConfig;
	}

	public void start() throws Exception {
		LOG.info("Starting server on port: " + config.getPort());
		server = new Server(createThreadPool());

		List<ServerConnector> connectors = createConnectors();
		for (ServerConnector connector : connectors) {
			server.addConnector(connector);
		}
		server.setHandler(createHandlers());

		// Extra options
		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);
		server.setStopTimeout(ALLOWED_SHUTDOWN_TIME);

		MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.addBean(mbContainer);

		// === jetty-lowresources.xml ===
		LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
		lowResourcesMonitor.setPeriod(1000);
		lowResourcesMonitor.setLowResourcesIdleTimeout(200);
		lowResourcesMonitor.setMonitorThreads(true);
		lowResourcesMonitor.setMaxConnections(0);
		lowResourcesMonitor.setMaxMemory(0);
		lowResourcesMonitor.setMaxLowResourcesTime(5000);
		server.addBean(lowResourcesMonitor);

		// === test-realm.xml ===
		// HashLoginService login = new HashLoginService();
		// login.setName("Test Realm");
		// login.setConfig(jetty_home + "/etc/realm.properties");
		// login.setRefreshInterval(0);
		// server.addBean(login);

		server.start();
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}

	private ThreadPool createThreadPool() {
		QueuedThreadPool _threadPool = new QueuedThreadPool();
		_threadPool.setName(SERVER_NAME);
		_threadPool.setMinThreads(config.getMinThreads());
		_threadPool.setMaxThreads(config.getMaxThreads());
		return _threadPool;
	}

	private List<ServerConnector> createConnectors() {
		List<ServerConnector> connectors = new ArrayList<ServerConnector>();

		// ======================================================================
		// HTTP Configuration
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(config.getSSLPort());
		http_config.setOutputBufferSize(32768);
		http_config.setRequestHeaderSize(8192);
		http_config.setResponseHeaderSize(8192);
		http_config.setSendServerVersion(true);
		http_config.setSendDateHeader(false);

		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
		http.setPort(config.getPort());
		http.setIdleTimeout(DEFAULT_MAXIMUM_IDLE_TIME);
		http.setSoLingerTime(-1);
		connectors.add(http);

		// ======================================================================
		// SSL Context Factory
		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath((!isRunningInShadedJar() ? (HOME_PATH_IDE + "classes/") : 
			config.getHomePath()) + "keystore");
		sslContextFactory.setKeyStorePassword("a12345678");
		sslContextFactory.setKeyManagerPassword("a12345678");
		sslContextFactory.setTrustStorePath((!isRunningInShadedJar() ? (HOME_PATH_IDE + "classes/") : 
			config.getHomePath()) + "keystore");
		sslContextFactory.setTrustStorePassword("a12345678");
		sslContextFactory.setExcludeCipherSuites(
				"SSL_RSA_WITH_DES_CBC_SHA", 
				"SSL_DHE_RSA_WITH_DES_CBC_SHA",
				"SSL_DHE_DSS_WITH_DES_CBC_SHA", 
				"SSL_RSA_EXPORT_WITH_RC4_40_MD5", 
				"SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
				"SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", 
				"SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

		// SSL HTTP Configuration
		HttpConfiguration https_config = new HttpConfiguration(http_config);
		https_config.addCustomizer(new SecureRequestCustomizer());

		// SSL Connector
		ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), 
				new HttpConnectionFactory(https_config));
		sslConnector.setPort(8443);
		connectors.add(sslConnector);

		return connectors;
	}

	private HandlerCollection createHandlers() {
		WebAppContext _ctx = new AliasEnhancedWebAppContext();
		_ctx.setResourceAlias("/WEB-INF/classes/", "/classes/");
		_ctx.setContextPath("/");

		if (isRunningInShadedJar()) {
			_ctx.setWar(getShadedWarUrl());
			_ctx.setBaseResource(new ResourceCollection(new String[] { getShadedWarUrl(), "." }));
			// _ctx.setBaseResource(Resource.newClassPathResource("META-INF/webapp"));
		} else {
			_ctx.setWar(PROJECT_RELATIVE_PATH_TO_WEBAPP);
			_ctx.setBaseResource(new ResourceCollection(new String[] { "./src/main/webapp", "./target/mariastore" }));
		}

		_ctx.setConfigurations(new Configuration[] {
		// This is necessary because Jetty out-of-the-box does not scan
		// the classpath of your project in Eclipse, so it doesn't find
		// your WebAppInitializer.
		new AnnotationConfiguration() {
			@Override
			public void configure(WebAppContext context) throws Exception {
				boolean metadataComplete = context.getMetaData().isMetaDataComplete();
				context.addDecorator(new AnnotationDecorator(context));

				// Even if metadata is complete, we still need to scan for
				// ServletContainerInitializers - if there are any
				AnnotationParser parser = null;
				if (!metadataComplete) {
					// If metadata isn't complete, if this is a servlet 3 webapp or isConfigDiscovered
					// is true, we need to search for annotations
					if (context.getServletContext().getEffectiveMajorVersion() >= 3
							|| context.isConfigurationDiscovered()) {
						_discoverableAnnotationHandlers.add(new WebServletAnnotationHandler(context));
						_discoverableAnnotationHandlers.add(new WebFilterAnnotationHandler(context));
						_discoverableAnnotationHandlers.add(new WebListenerAnnotationHandler(context));
					}
				}

				// Regardless of metadata, if there are any ServletContainerInitializers with @HandlesTypes, 
				// then we need to scan all the classes so we can call their onStartup() methods correctly
				createServletContainerInitializerAnnotationHandlers(context, getNonExcludedInitializers(context));

				if (!_discoverableAnnotationHandlers.isEmpty() || _classInheritanceHandler != null
						|| !_containerInitializerAnnotationHandlers.isEmpty()) {
					parser = createAnnotationParser();

					parse(context, parser);

					for (DiscoverableAnnotationHandler h : _discoverableAnnotationHandlers)
						context.getMetaData().addDiscoveredAnnotations(
								((AbstractDiscoverableAnnotationHandler) h).getAnnotationList());
				}

			}

			private void parse(final WebAppContext context, AnnotationParser parser) throws Exception {
				List<Resource> _resources = getResources(getClass().getClassLoader());
				for (Resource _resource : _resources) {
					if (_resource == null)
						return;

					parser.clearHandlers();
					for (DiscoverableAnnotationHandler h : _discoverableAnnotationHandlers) {
						if (h instanceof AbstractDiscoverableAnnotationHandler)
							((AbstractDiscoverableAnnotationHandler) h).setResource(null);
					}
					parser.registerHandlers(_discoverableAnnotationHandlers);
					parser.registerHandler(_classInheritanceHandler);
					parser.registerHandlers(_containerInitializerAnnotationHandlers);

					final ClassNameResolver resolver = new ClassNameResolver() {
						public boolean isExcluded(String name) {
							if (context.isSystemClass(name)) return true;
							if (context.isServerClass(name)) return false;
							return false;
						}

						public boolean shouldOverride(String name) {
							// looking at webapp classpath, found already-parsed class of same 
							// name - did it come from system or duplicate in webapp?
							if (context.isParentLoaderPriority()) return false;
							return true;
						}
					};

					if (_resource.isDirectory()) {
						parser.parseDir(_resource, resolver);
					} else if ( isExtensionJar(_resource) ){
						parser.parseJar(_resource, resolver);
					}
				}
			}

			private List<Resource> getResources(ClassLoader aLoader) throws IOException {
				if (aLoader instanceof URLClassLoader) {
					List<Resource> _result = new ArrayList<Resource>();
					URL[] _urls = ((URLClassLoader) aLoader).getURLs();
					for (URL _url : _urls)
						_result.add(Resource.newResource(_url));

					return _result;
				}
				return Collections.emptyList();
			}
		} });

		// Handler Structure
		HandlerCollection handlers = new HandlerCollection();
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { _ctx });
		handlers.setHandlers(new Handler[] { contexts, createRequestLog(), new DefaultHandler() });
		server.setHandler(handlers);

		return handlers;
	}
	
	/**
	 * Simple policy to search Servlet 3 extensions in jars by name
	 * @param resource
	 * @return <code>true</code> it jar need to be searched for Servlet 3 annotations
	 */
	private boolean isExtensionJar(Resource resource) {
		JarInfo jar = SBTJarUtils.parseJarVersion(resource.getName());
		for (String name : extensionJars) {
			if (jar.getName().endsWith(name)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Create Jetty request log handler implementation. Note: we can implement
	 * it through SLF4J as well to have all logs in one place.
	 * 
	 * @return Jetty request log handler implementation
	 */
	private RequestLogHandler createRequestLog() {
		NCSARequestLog requestLog = new NCSARequestLog();
		File _logPath = new File((!isRunningInShadedJar() ? HOME_PATH_IDE : config.getHomePath()) + LOG_NAME);
		_logPath.getParentFile().mkdirs();
		
		requestLog.setFilename(_logPath.getPath());
		requestLog.setFilenameDateFormat("yyyy_MM_dd");
		requestLog.setRetainDays(90);
		requestLog.setAppend(true);
		requestLog.setExtended(false);
		requestLog.setLogCookies(false);
		requestLog.setLogTimeZone("GMT");
		RequestLogHandler requestLogHandler = new RequestLogHandler();
		requestLogHandler.setRequestLog(requestLog);
		return requestLogHandler;
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
		return Thread.currentThread().getContextClassLoader().getResource(aResource);
	}

	private String getShadedWarUrl() {
		String _urlStr = getResource(WEB_XML).toString();
		return _urlStr.substring(0, _urlStr.length() - WEB_XML.length());
	}
}