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
package com.dm.estore.common.config;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.event.ConfigurationErrorEvent;
import org.apache.commons.configuration.event.ConfigurationErrorListener;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorSystem;
import ch.qos.logback.classic.Level;
import ch.qos.logback.core.joran.spi.JoranException;

import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.common.utils.FileUtils;
import com.dm.estore.common.utils.LogUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Application configuration service
 *
 * @author dmorozov
 *
 */
public class Cfg {

    private static final Logger LOG = LoggerFactory.getLogger(Cfg.class);

    /**
     * System user home property name.
     */
    private static final String USER_HOME = "user.home";

    /**
     * Default application root folder.
     */
    private static final String APP_HOME = "." + CommonConstants.App.CFG_APP_NAME;

    /**
     * Root configuration file name.
     */
    private static final String ROOT_CONFIG_NAME = "defaultConfig/config.xml";

    /**
     * Root configuration file name.
     */
    private static final String RELOADABLE_CONFIG_NAME = "app.xml";
    
    public static final String CMD_PRINT_CONFIG = "config";

    /**
     * Default configuration files.
     */
    private static final String[] CONFIG_FILES = {
        ROOT_CONFIG_NAME,
        "defaultConfig/app-default.xml",
        "defaultConfig/logback.xml",
        RELOADABLE_CONFIG_NAME
    };

    private final ActorSystem rootActorSystem;
    
    private final WatchService watchService;

    private final Properties overrideProperties;

    private final Configuration config;

    private volatile boolean keepWatching = true;
    
    private ConfigurationChangeCallback callback;

    /**
     * Resolved configuration home folder.
     */
    private String configurationHome;

    /**
     * Last reload time.
     */
    private static Date lastReloadTime;

    public interface ConfigurationChangeCallback {
        void onChange();
    }

    public static class Factory {

        public static synchronized Cfg createConfiguration(final Properties overrideProperties) {
            if (instance == null) {
                try {
                    instance = new Cfg(overrideProperties);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to initialize configuration service", e);
                }
            }
            return instance;
        }
    }

    private static Cfg instance;

    public static Cfg instance() {
        return instance;
    }

    private Cfg(final Properties overrideProperties) throws IOException {
        this.overrideProperties = overrideProperties;
        this.watchService = FileSystems.getDefault().newWatchService();

        resetLogger();
        this.config = createConfiguration();
        this.rootActorSystem = createActorSystem();
        
        startWatcher(configurationHome);
        reloadLogConfiguration();
    }
    
    private ActorSystem createActorSystem() {
        final String akkaConfigPath = getHomeFolder() + File.separator + getConfig().getString(CommonConstants.Cfg.CFG_AKKA_CFG, CommonConstants.Cfg.Defaults.DEFAULT_AKKA_CFG);
        final Config akkaConfig = StringUtils.isEmpty(akkaConfigPath) || !new File(akkaConfigPath).exists() ? 
                ConfigFactory.load() : ConfigFactory.load(akkaConfigPath);
        return ActorSystem.create(CommonConstants.App.CFG_APP_NAME, akkaConfig);
    }

    private Configuration createConfiguration() {
        try {
            final CompositeConfiguration compositeConfig = new CompositeConfiguration();

            String configuredHome = null;
            if (overrideProperties != null) {
                MapConfiguration initialConfig = new MapConfiguration(overrideProperties);
                compositeConfig.addConfiguration(initialConfig);

                if (overrideProperties.containsKey(CommonConstants.Cfg.CFG_HOME_FOLDER)) {
                    configuredHome = overrideProperties.getProperty(CommonConstants.Cfg.CFG_HOME_FOLDER);
                }
            }

            configurationHome = getOrCreateHomeFolder(configuredHome);

            final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder(configurationHome + File.separator + ROOT_CONFIG_NAME);
            final CombinedConfiguration reloadableConfig = builder.getConfiguration(true);
            ConfigurationLogListener listener = new ConfigurationLogListener();
            reloadableConfig.addConfigurationListener(listener);
            reloadableConfig.addErrorListener(listener);
            // reloadableConfig.setForceReloadCheck(true);
            compositeConfig.addConfiguration(reloadableConfig);

            lastReloadTime = new Date();
            return compositeConfig;
        } catch (ConfigurationException e) {
            LOG.error("Unable to load configuration", e);
        }

        return null;
    }

    public void setCallback(final ConfigurationChangeCallback callback) {
        this.callback = callback;
    }

    public Configuration getConfig() {
        return config;
    }

    public Properties getAllProperties() {
        return ConfigurationConverter.getProperties(config);
    }

    private String getOrCreateHomeFolder(final String configuredHome) {
        String homeFolder = configuredHome;
        if (StringUtils.isEmpty(homeFolder)) {
            homeFolder = System.getProperty(CommonConstants.Cfg.CFG_HOME_FOLDER);
        }
        if (StringUtils.isEmpty(homeFolder)) {
            homeFolder = System.getProperty(USER_HOME) + File.separator + APP_HOME;
        }
        
        FileUtils.createFilesFromClasspath(homeFolder, Arrays.asList(CONFIG_FILES),
        		Arrays.asList(new String[]{RELOADABLE_CONFIG_NAME}));

        return homeFolder;
    }

    private void startWatcher(final String configDirPath) throws IOException {
        Path path = Paths.get(configDirPath);
        path.register(watchService, ENTRY_MODIFY);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    keepWatching = false;
                    watchService.close();
                } catch (IOException e) {
                    LOG.error("Unable to stop configuration watch service", e);
                }
            }
        });

        FutureTask<Integer> watchTask = new FutureTask<>(new Callable<Integer>() {
            private int totalEventCount;

            @Override
            public Integer call() throws Exception {
                while (keepWatching) {
                    try {
                        WatchKey watchKey = watchService.poll(5, TimeUnit.SECONDS);
                        if (watchKey != null) {
                            boolean updateConfiguration = false;
                            for (WatchEvent<?> event : watchKey.pollEvents()) {
                            	LOG.debug("Configuration changed: " + event.kind());
                                updateConfiguration = true;
                                totalEventCount++;
                            }
                            if (!watchKey.reset()) {
                                // handle situation no longer valid
                                keepWatching = false;
                            } else {
                                if (updateConfiguration) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(1000);
                                                getConfig().getString(CommonConstants.Cfg.CFG_UPDATE_TRIGGER);
                                            } catch (InterruptedException ex) {
                                                // do nothing
                                            }
                                        }
                                    }).start();
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                return totalEventCount;
            }
        });
        new Thread(watchTask).start();
    }

    class ConfigurationLogListener implements ConfigurationListener, ConfigurationErrorListener {

        @Override
        public void configurationChanged(ConfigurationEvent event) {
            if (!event.isBeforeUpdate()) {
                LOG.debug("Configuration changed.");
                lastReloadTime = new Date();

                try {
                    if (callback != null) {
                        callback.onChange();
                    }
                } catch (Exception e) {
                    LOG.error("Failed to handle configuration update", e);
                }
            }
        }

        @Override
        public void configurationError(ConfigurationErrorEvent event) {
            // Log the standard properties of the configuration event
            configurationChanged(event);
            // Now log the exception
            LOG.error("Configuration service failed", event.getCause());
        }
    }
    
    private void resetLogger() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.WARN);
    }
    
    private void reloadLogConfiguration() {
        try {
            final String configPath = getHomeFolder() + File.separator + getConfig().getString(CommonConstants.Cfg.CFG_LOG_CONFIG);
            final String logFolder = getHomeFolder() + File.separator + getConfig().getString(CommonConstants.Cfg.CFG_LOG_DIR);
            
            LOG.info("Load logger configuration: " + configPath);
            LogUtils.updateLoggerConfiguration(configPath, logFolder);
        } catch (JoranException ex) {
            LOG.error("Unable to reload log configuration", ex);
        }
    }
    
    /**
     * Get current Application configuration folder.
     *
     * @return Application configuration folder.
     */
    public String getHomeFolder() {
        return configurationHome;
    }

    public static Date getLastReloadTime() {
        return lastReloadTime;
    }
    
    public ActorSystem actorSystem() {
        return rootActorSystem;
    }

    public void shutdown() {
        try {
            if (rootActorSystem != null) {
                rootActorSystem.shutdown();
                rootActorSystem.awaitTermination();
            }
        } catch (Exception e) {
            LOG.error("Unable to stop actors system", e);
        }

        try {
            keepWatching = false;
            watchService.close();
        } catch (IOException e) {
            LOG.error("Unable to stop configuration watch service", e);
        }
    }

	public void print() {
		
		Properties systemProperties = System.getProperties();
		
		final String eol = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();

		sb.append("Configuration").append(eol).append("General configuration").append(eol);		
		
		String key = null;
		for (Iterator<String> iter = getConfig().getKeys(); iter.hasNext(); key = iter.next()) {
			if (key != null && !Cfg.CMD_PRINT_CONFIG.equalsIgnoreCase(key) && !systemProperties.containsKey(key)) {
				sb.append("    " + key + " = " + getConfig().getString(key)).append(eol);
			}
		}

		sb.append(eol).append("Akka configuration").append(eol);
		sb.append(actorSystem().settings().toString());
		
		LOG.info(sb.toString());
	}
}
