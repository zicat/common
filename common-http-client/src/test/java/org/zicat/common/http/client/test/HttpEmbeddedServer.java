package org.zicat.common.http.client.test;

import org.apache.http.config.SocketConfig;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;


public class HttpEmbeddedServer {
	
	public HttpServer server;
	
	public void buildAndStart(Map<String, HttpRequestHandler> registerHandlerList) throws IOException, InterruptedException {
		
		final SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(15000).build();
		final ServerBootstrap bootstrp = ServerBootstrap.bootstrap().setSocketConfig(socketConfig).setServerInfo("TEST/1.1");
		if(registerHandlerList != null) {
			for(Entry<String, HttpRequestHandler> entry: registerHandlerList.entrySet()) {
				bootstrp.registerHandler(entry.getKey(), entry.getValue());
			}
		}
		
		server = bootstrp.create();
		server.start();
	}
	
	public int getLocalPort() {
		return server.getLocalPort();
	}
	
	public void shutdown() {
		
		if(server != null)
			server.shutdown(5, TimeUnit.SECONDS);
	}
}
