package org.zicat.common.http.client.test.factory.jersey;

import org.zicat.common.http.client.factory.ClientFactory;
import org.zicat.common.http.client.factory.jersey.DefaultJerseyClientFactory;
import org.zicat.common.http.client.factory.jersey.GrizzlyJerseyClientFactory;
import org.zicat.common.http.client.factory.jersey.JettyJerseyClientFactory;
import junit.framework.Assert;

import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class JerseyClientFactoryTest {
	
	@Test
	public void test() {
		
		ClientFactory factory = new JettyJerseyClientFactory(10, 1000, 1000);
		testing(factory);
		
		factory = new GrizzlyJerseyClientFactory(10, 1000, 1000);
		testing(factory);
		
		factory = new DefaultJerseyClientFactory(10, 1000, 1000);
		testing(factory);
	}
	
	private void testing(ClientFactory factory) {
		
		Client client = factory.createClient();
		Assert.assertNotNull(client);
		
		factory.destory(null);
		factory.destory(client);
		
		client = ClientBuilder.newClient();
		factory.destory(client);
	}
}

