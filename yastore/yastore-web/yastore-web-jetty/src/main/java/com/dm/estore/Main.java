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
package com.dm.estore;

import com.dm.estore.common.config.Cfg;
import com.dm.estore.common.constants.CommonConstants;
import com.dm.estore.server.CommandLineOptions;
import com.dm.estore.server.WebServer;
import com.dm.estore.server.WebServerConfig;
import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author dmorozov
 */
public class Main {

    private WebServer server;

    public static void main(String... args) throws Exception {
        CommandLineOptions cmd = new CommandLineOptions();
        Properties commandLine = cmd.parse(CommonConstants.App.CFG_APP_NAME, args);
        if (commandLine != null) {
    		new Main(commandLine).start();
    	}
    }

    public Main(Properties commandLine) {
        createApplication(commandLine);
    }

    private void createApplication(Properties commandLine) {
        try {
            prepareCommandArguments(commandLine);
            
            Cfg configuration = Cfg.Factory.createConfiguration(commandLine);
            if (configuration.getConfig().getBoolean("config", Boolean.FALSE)) {
            	configuration.print();
            	System.exit(1);
            }
            
            server = createWebServer(configuration);
        } catch (Exception e) {
            System.out.println("Unable to start server.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected String resolveDefaultConfigFolder() {
        String baseFolder = Paths.get("").toAbsolutePath().toString();
        return baseFolder;
    }

    protected void prepareCommandArguments(final Properties commandLine) {
        if (!commandLine.containsKey(CommonConstants.Cfg.CFG_HOME_FOLDER)) {
            String baseFolder = resolveDefaultConfigFolder();
            baseFolder += File.separator + CommonConstants.Cfg.Defaults.DEFAULT_CONFIG_DIR;
            commandLine.setProperty(CommonConstants.Cfg.CFG_HOME_FOLDER, baseFolder);
        }
    }

    protected WebServer createWebServer(Cfg configuration) {
        WebServer webServer = new WebServer(WebServerConfig.Factory.newProductionConfig(
                configuration.getConfig().getInt(CommonConstants.Server.PROP_PORT, CommonConstants.Server.Defaults.DEFAULT_HTTP_PORT),
                configuration.getConfig().getString(CommonConstants.Server.PROP_INTERFACE, CommonConstants.Server.Defaults.DEFAULT_INTERFACE),
                configuration.getConfig().getInt(CommonConstants.Server.PROP_MIN_THREADS, CommonConstants.Server.Defaults.DEFAULT_MIN_THREADS),
                configuration.getConfig().getInt(CommonConstants.Server.PROP_MAX_THREADS, CommonConstants.Server.Defaults.DEFAULT_MAX_THREADS)));
        webServer.setRunningInShadedJar(Boolean.TRUE);
        return webServer;
    }

    public void start() throws Exception {
        server.start();
        server.join();
    }

    protected WebServer getServer() {
        return server;
    }
}
