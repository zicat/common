package com.newegg.ec.ncommon.http.client.factory.jersey;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.spi.ConnectorProvider;

/**
 * 
 * @author lz31
 *
 */
public class DefaultJerseyClientFactory extends JerseyClientFactory {
	
	protected int connectionTimeout;
	protected int readTimeout;
	
	public DefaultJerseyClientFactory(int aSynHttpThreadCount, int connectionTimeout, int readTimeout) {
		
		super(aSynHttpThreadCount);
		this.connectionTimeout = connectionTimeout;
		this.readTimeout = readTimeout;
	}
	
	@Override
	protected ClientConfig buildDefaultConfig() {
		
		ClientConfig config = super.buildDefaultConfig();
		config.property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout);
		config.property(ClientProperties.READ_TIMEOUT, readTimeout);
		config.property(ClientProperties.BACKGROUND_SCHEDULER_THREADPOOL_SIZE, aSynHttpThreadCount / 2);
		config.property(ClientProperties.ASYNC_THREADPOOL_SIZE, aSynHttpThreadCount);
		return config;
	}

	@Override
	protected ConnectorProvider newProvider() {
		return new HttpUrlConnectorProvider().useSetMethodWorkaround();
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
}