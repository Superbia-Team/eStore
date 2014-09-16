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

import com.dm.estore.common.constants.CommonConstants;

/**
 * @author dmorozov
 */
public interface WebServerConfig
{
    public String getServerName();

    public int getPort();

    public String getHostInterface();

    public int getMinThreads();

    public int getMaxThreads();

    public String getAccessLogDirectory();

    int getSSLPort();

    public class Factory {

        public static final String SERVER_NAME = "eStore";
        public static final String DEFAULT_LOGS_FOLDER = "./build/logs/";

        public static WebServerConfig newDevelopmentConfig(int serverPort, String serverInterface) {
            return new Development(serverPort, serverInterface);
        }

        public static WebServerConfig newProductionConfig(int serverPort, String serverInterface, int minThreads, int maxThreads) {
            return new Production(serverPort, serverInterface, minThreads, maxThreads);
        }

        static abstract class AbstractWebServerConfig implements WebServerConfig
        {
            private int port;
            private int sslPort;
            private String serverInterface;
            private int minThreads;
            private int maxThreads;

            private AbstractWebServerConfig(int serverPort, String serverInterface, int minThreads, int maxThreads, int sslPort)
            {
                port = serverPort;
                this.serverInterface = serverInterface;
                this.minThreads = minThreads;
                this.maxThreads = maxThreads;
                this.sslPort = sslPort;
            }

            @Override
            public String getServerName()
            {
                return SERVER_NAME;
            }

            @Override
            public int getPort()
            {
                return port;
            }

            @Override
            public String getHostInterface()
            {
                return serverInterface;
            }

            @Override
            public int getMinThreads()
            {
                return minThreads;
            }

            @Override
            public int getMaxThreads()
            {
                return maxThreads;
            }

            @Override
            public String getAccessLogDirectory()
            {
                return DEFAULT_LOGS_FOLDER;
            }

            @Override
            public int getSSLPort()
            {
                return sslPort;
            }
        }

        public static final class Development extends AbstractWebServerConfig
        {
            public Development(int aPort, String anInterface)
            {
                super(aPort, anInterface, CommonConstants.Server.Defaults.DEFAULT_MIN_THREADS, 
                        CommonConstants.Server.Defaults.DEFAULT_MAX_THREADS, CommonConstants.Server.Defaults.DEFAULT_HTTP_PORT_SSL);
            }
        }

        public static final class Production extends AbstractWebServerConfig
        {
            public Production(int aPort, String anInterface, int aMinThreads, int aMaxThreads)
            {
                super(aPort, anInterface, aMinThreads, aMaxThreads, CommonConstants.Server.Defaults.DEFAULT_HTTP_PORT_SSL);
            }
        }
    }
}
