package org.zicat.common.http.client.factory.jersey;

import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.glassfish.jersey.jetty.connector.JettyConnectorProvider;

/**
 * 
 * @author lz31
 *
 */
public class JettyJerseyClientFactory extends DefaultJerseyClientFactory {

	public JettyJerseyClientFactory(int aSynHttpThreadCount, int connectionTimeout, int readTimeout) {
		super(aSynHttpThreadCount, connectionTimeout, readTimeout);
	}
	
	@Override
	protected ConnectorProvider newProvider() {
		return new JettyConnectorProvider();
	}
}
