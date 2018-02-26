package org.zicat.common.http.client.factory.jersey;

import javax.ws.rs.core.Configuration;

import org.zicat.common.http.client.factory.AbstractClientFactory;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.spi.ConnectorProvider;

/**
 * Created by lz31 on 2017/9/21.
 */
public abstract class JerseyClientFactory extends AbstractClientFactory {

    public JerseyClientFactory(int aSynHttpThreadCount) {
    	super(aSynHttpThreadCount);
    }

    /**
     * class can implements JerseyClientFactory.class and set property and property value
     * @return
     */
    protected ClientConfig buildDefaultConfig() {
    	
    	 ClientConfig config = new ClientConfig();
         config = config.connectorProvider(newProvider());
         return config;
    }
    
    protected abstract ConnectorProvider newProvider();
    
	@Override
	protected Configuration buildConfig() {
		return buildDefaultConfig();
	}
}

