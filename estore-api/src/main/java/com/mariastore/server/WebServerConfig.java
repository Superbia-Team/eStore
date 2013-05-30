package com.mariastore.server;

public interface WebServerConfig {
	
	public int getPort();
	
	public int getSSLPort();

	public int getMinThreads();

	public int getMaxThreads();
	
	public String getHomePath();

	public class Factory {
		
		private static final int DEFAULT_PORT = 8080;
		private static final int DEFAULT_SSL_PORT = 8443;
		private static final int DEFAULT_MIN_THREADS = 5;
		private static final int DEFAULT_MAX_THREADS = 15;
		private static final String DEFAULT_HOME_PATH = "/tmp/";
		
		public static WebServerConfig newConfig() {
			return new WebServerConfigImpl();
		}
		
		static abstract class AbstractWebServerConfig implements WebServerConfig {
			private int port;
			private int minThreads;
			private int maxThreads;
			private int sslPort;

			private AbstractWebServerConfig(int port, int sslPort, int aMinThreads, int aMaxThreads) {
				this.port = port;
				this.minThreads = aMinThreads;
				this.maxThreads = aMaxThreads;
				this.sslPort = sslPort;
			}

			@Override
			public int getPort() {
				return port;
			}

			@Override
			public int getMinThreads() {
				return minThreads;
			}

			@Override
			public int getMaxThreads() {
				return maxThreads;
			}
			
			@Override
			public String getHomePath() {
				return DEFAULT_HOME_PATH;
			}

			public int getSSLPort() {
				return sslPort;
			}
		}

		public static final class WebServerConfigImpl extends AbstractWebServerConfig {
			// TODO: read from properties
			public WebServerConfigImpl() {
				super(DEFAULT_PORT, DEFAULT_SSL_PORT, DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS);
			}
		}
	}
}
