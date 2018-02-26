package com.newegg.ec.ncommon.http.client.factory.jersey;

import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;

/**
 * 
 * @author lz31
 *
 */
public class GrizzlyJerseyClientFactory extends DefaultJerseyClientFactory {

	public GrizzlyJerseyClientFactory(int aSynHttpThreadCount, int connectionTimeout, int readTimeout) {
		super(aSynHttpThreadCount, connectionTimeout, readTimeout);
	}
	
	@Override
	protected ConnectorProvider newProvider() {
		return new GrizzlyConnectorProvider();
	}
}
