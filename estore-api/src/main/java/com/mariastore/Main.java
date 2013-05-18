package com.mariastore;

import com.mariastore.server.WebServer;
import com.mariastore.server.WebServerConfig;

public class Main {
	
	public static void main(String... anArgs) throws Exception {
		new Main().start();
	}

	private WebServer server;

	public Main() {
		server = new WebServer(WebServerConfig.Factory.newConfig());
	}

	public void start() throws Exception {
		server.start();
		server.join();
	}
}
