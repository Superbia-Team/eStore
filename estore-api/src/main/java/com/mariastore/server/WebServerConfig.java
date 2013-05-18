package com.mariastore.server;

public interface WebServerConfig {
	
	public int getPort();

	public String getHostInterface();

	public int getMinThreads();

	public int getMaxThreads();
	
	public String getLogPath();

	public class Factory {
		
		private static final int DEFAULT_PORT = 8080;
		private static final String DEFAULT_INTERFACE = "localhost";
		private static final int DEFAULT_MIN_THREADS = 5;
		private static final int DEFAULT_MAX_THREADS = 15;
		private static final String DEFAULT_LOG_PATH = "/tmp/mariastore_http_yyyy_mm_dd.log";
		
		public static WebServerConfig newConfig() {
			return new WebServerConfigImpl();
		}
		
		static abstract class AbstractWebServerConfig implements WebServerConfig {
			private int port;
			private String intf;
			private int minThreads;
			private int maxThreads;

			private AbstractWebServerConfig(int aPort, String anInterface, int aMinThreads, int aMaxThreads) {
				port = aPort;
				intf = anInterface;
				minThreads = aMinThreads;
				maxThreads = aMaxThreads;
			}

			@Override
			public int getPort() {
				return port;
			}

			@Override
			public String getHostInterface() {
				return intf;
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
			public String getLogPath() {
				return DEFAULT_LOG_PATH;
			}
		}

		public static final class WebServerConfigImpl extends AbstractWebServerConfig {
			// TODO: read from properties
			public WebServerConfigImpl() {
				super(DEFAULT_PORT, DEFAULT_INTERFACE, DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS);
			}
		}
	}
}
