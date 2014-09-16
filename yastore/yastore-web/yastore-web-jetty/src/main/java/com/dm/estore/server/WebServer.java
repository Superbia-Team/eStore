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
package com.dm.estore.server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dm.estore.common.constants.CommonConstants;

/**
 * @author dmorozov
 */
public class WebServer {

    private static final Logger LOG = LoggerFactory.getLogger(WebServer.class);
    private static final String WEB_XML = "WEB-INF/web.xml";

    private Server server;
    private final WebServerConfig config;
    private boolean runningInShadedJar;

    public static interface WebContext {

        public File getWarPath();

        public String getContextPath();
    }

    public WebServer(WebServerConfig aConfig) {
        config = aConfig;
    }

    public void start() throws Exception {
        LOG.info("Start '" + CommonConstants.App.CFG_APP_NAME + "' Web server ...");
        server = new Server(createThreadPool());

        List<ServerConnector> connectors = createConnectors();
        for (ServerConnector connector : connectors) {
            server.addConnector(connector);
        }

        server.setHandler(createHandlers());
        server.setStopAtShutdown(true);

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
        _threadPool.setName(config.getServerName());
        _threadPool.setMinThreads(config.getMinThreads());
        _threadPool.setMaxThreads(config.getMaxThreads());
        return _threadPool;
    }

    private List<ServerConnector> createConnectors() {
        List<ServerConnector> connectors = new ArrayList<>();

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
        http.setSoLingerTime(-1);
        connectors.add(http);

        // ======================================================================
        // SSL Context Factory
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(getResource("keystore").toString());
        sslContextFactory.setKeyStorePassword("a12345678");
        sslContextFactory.setKeyManagerPassword("a12345678");
        sslContextFactory.setTrustStorePath(getResource("keystore").toString());
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
        sslConnector.setPort(config.getSSLPort());
        connectors.add(sslConnector);

        return connectors;
    }

    private URL getResource(String resource) {
        return WebServer.class.getClassLoader().getResource(resource);
    }
    
    private void fixDefaultConfigurationFiles() {
    	for (int i = 0; i < WebAppContext.DEFAULT_CONFIGURATION_CLASSES.length; i++) {
    		if ("org.eclipse.jetty.webapp.MetaInfConfiguration".equalsIgnoreCase(WebAppContext.DEFAULT_CONFIGURATION_CLASSES[i])) {
    			WebAppContext.DEFAULT_CONFIGURATION_CLASSES[i] = "com.dm.estore.server.ProjectsMetaInfConfiguration";
    			break;
    		}
    	}
    	
    }

    private HandlerCollection createHandlers() {
    	if (!runningInShadedJar) {
            Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
            fixDefaultConfigurationFiles();
    	}
    	
        WebAppContext _ctx = new WebAppContext();
        _ctx.setContextPath("/");
        
        org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
        classlist.addAfter(
                "org.eclipse.jetty.webapp.FragmentConfiguration",
                "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        
        if (runningInShadedJar) {
            try {
                URL location = new URL(getShadedWarUrl());
                _ctx.setWar(location.toExternalForm());

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

        } else {
            String warLocation = System.getProperty("webapp.war.path", "../yastore-web-app/src/main/webapp");
            _ctx.setWar(warLocation);
            //_ctx.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/javax.servlet-[^/]*\\.jar$|.*/servlet-api-[^/]*\\.jar$|.*/classes/.*");
            // _ctx.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/*\\.jar$");
            _ctx.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/*\\.jar$|.*/classes/eclipse/.*");
            
            // Patch for Eclipse IDE :(
            classlist.addBefore("org.eclipse.jetty.webapp.FragmentConfiguration", "com.dm.estore.server.WebFragmentProjectConfiguration");
        }

        classlist.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");

        List<Handler> _handlers = new ArrayList<>();

        _handlers.add(_ctx);

        HandlerList _contexts = new HandlerList();
        _contexts.setHandlers(_handlers.toArray(new Handler[0]));

        RequestLogHandler _log = new RequestLogHandler();
        _log.setRequestLog(createRequestLog());

        HandlerCollection _result = new HandlerCollection();
        _result.setHandlers(new Handler[]{_contexts, _log});

        return _result;
    }

    private RequestLog createRequestLog() {
        NCSARequestLog _log = new NCSARequestLog();

        File _logPath = new File(config.getAccessLogDirectory() + "yyyy_mm_dd.request.log");
        _logPath.getParentFile().mkdirs();

        _log.setFilename(_logPath.getPath());
        _log.setRetainDays(30);
        _log.setExtended(false);
        _log.setAppend(true);
        _log.setLogTimeZone("UTC");
        _log.setLogLatency(true);
        return _log;
    }

    private String getShadedWarUrl() {
        String _urlStr = getResource(WEB_XML).toString();
        return _urlStr.substring(0, _urlStr.length() - WEB_XML.length());
    }

    public void setRunningInShadedJar(boolean runningInShadedJar) {
        this.runningInShadedJar = runningInShadedJar;
    }
}
