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

package com.dm.estore.common.constants;

import java.util.Locale;

/**
 * @author dmorozov
 */
public class CommonConstants {
	
	public static final String HOME_PLACEHOLDER = "$APP_HOME";

	public static class Events {
		public static final String CHANGE_CONFIGURATION = "configuration-changed-event";
		public static final String CHANGE_MODEL = "model-changed-event";
	}
	
	public static class i18n {
		public static final String DEFAULT_ENCODING = "UTF-8";
		public static final Locale DEFAULT_LOCALE = Locale.US;
		public static final String PARAM_CHANGE_LOCALE = "lang";
	}

	public static class Server {
		public static final String PROP_INTERFACE = "server.interface";
		public static final String PROP_PORT = "server.port";
		public static final String PROP_PORT_SSL = "server.port.ssl";
		public static final String PROP_PROTOCOL_SSL = "server.protocol.ssl";
		public static final String PROP_MIN_THREADS = "server.threads.min";
		public static final String PROP_MAX_THREADS = "server.threads.max";

		public static class Defaults {
			public static final int DEFAULT_HTTP_PORT = 8080;
			public static final int DEFAULT_HTTP_PORT_SSL = 8443;
			public static final String DEFAULT_INTERFACE = "localhost";
			public static final int DEFAULT_MIN_THREADS = 5;
			public static final int DEFAULT_MAX_THREADS = 200;
		}
	}

	public static class Cfg {
		public static final String CFG_HOME_FOLDER = "app.home";
		public static final String CFG_LOG_CONFIG = "log/logConfigFile";
		public static final String CFG_LOG_DIR = "log/logFolder";
		public static final String CFG_UPDATE_TRIGGER = "*/updateTrigger";
		public static final String CFG_LOG_TRACE_SQL = "log/traceSQL";
		public static final String CFG_LOG_FORMAT_SQL = "log/formatSQL";

		public static final String CFG_AKKA_CFG = "akka/config";

		public static class Search {
			public static final String CFG_SERVER_TYPE = "search/serverType";
			public static final String CFG_CLOUD_URL = "search/cloud/url";
			public static final String CFG_CLOUD_CFG_NAME = "search/cloud/collectionConfigName";
		}
		
		public static class DB {
			public static final String CFG_DIALECT = "dataSource/dialect";
			public static final String CFG_JDBC_URL = "dataSource/jdbcUrl";
			public static final String CFG_JDBC_DRIVER = "dataSource/driverClassName";
			public static final String CFG_USERNAME = "dataSource/userName";
			public static final String CFG_PSWD = "dataSource/password";
			
			public static final String CFG_IDLE_PERIOD = "dataSource/idleConnectionTestPeriod";
			public static final String CFG_IDLE_MAX_AGE = "dataSource/dleMaxAge";
			public static final String CFG_MAX_CON_PER_PART = "dataSource/maxConnectionsPerPartition";
			public static final String CFG_MIN_CON_PER_PART = "dataSource/minConnectionsPerPartition";
			public static final String CFG_PART_COUNT = "dataSource/partitionCount";
			public static final String CFG_ACQ_INC = "dataSource/acquireIncrement";
			public static final String CFG_STMT_CACHE = "dataSource/statementsCacheSize";
		}

		public static class Defaults {
			public static final String DEFAULT_CONFIG_DIR = "config";
			public static final String DEFAULT_AKKA_CFG = "defaultConfig/akka.conf";
		}
	}

	public static class App {
		public static final String CFG_APP_NAME = "yastore";
	}

	public static class Defaults {
		// public static final String DEFAULT_APP_CONFIG = App.CFG_APP_NAME +
		// ".properties";
	}
}
