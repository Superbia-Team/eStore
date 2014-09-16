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
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author dmorozov
 */
public class MainIDE extends Main {

    public static void main(String... args) throws Exception {
        CommandLineOptions cmd = new CommandLineOptions();
        Properties commandLine = cmd.parse(CommonConstants.App.CFG_APP_NAME, args);

        new MainIDE(commandLine).start();
    }

    public MainIDE(Properties commandLine) {
        super(commandLine);
    }

    @Override
    protected WebServer createWebServer(Cfg configuration) {
        final WebServer server = super.createWebServer(configuration);
        server.setRunningInShadedJar(Boolean.FALSE);
        return server;
    }

    @Override
    protected String resolveDefaultConfigFolder() {
        return Paths.get("").toAbsolutePath().toString() + File.separator + "build";
    }

    @Override
    public void start() throws Exception {
        getServer().start();

        new Thread() {
            @Override
            public void run() {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    in.readLine();
                    getServer().stop();
                    in.close();

                    Cfg.instance().shutdown();
                } catch (Exception ex) {
                    System.out.println("Failed to stop Jetty");
                }
            }
        }.start();

        getServer().join();
    }
}
