package com.newegg.ec.ncommon.http.client.test.factory.jersey;

import com.newegg.ec.ncommon.http.client.RestfullClient;
import com.newegg.ec.ncommon.http.client.factory.AbstractClientFactory;
import com.newegg.ec.ncommon.http.client.factory.jersey.DefaultJerseyClientFactory;
import com.newegg.ec.ncommon.http.client.test.EchoHandler;
import com.newegg.ec.ncommon.http.client.test.HttpEmbeddedServer;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DefaultJerseyClientFactoryTest {
	
	protected static HttpEmbeddedServer server = new HttpEmbeddedServer();
	protected static AbstractClientFactory factory = new DefaultJerseyClientFactory(20, 2000, 2000);
	
	@BeforeClass
	public static void beforeClass() throws IOException, InterruptedException {
		
		final Map<String, HttpRequestHandler> registerHandlerList = new HashMap<>();
		registerHandlerList.put("/*", new EchoHandler());
		server.buildAndStart(registerHandlerList);
	}
	
	@Test
	public void test() throws Exception {
		
		
		RestfullClient client = new RestfullClient(factory);
		String path = "/echo/cc";
		String stringEnitiy = new String("string entity");
		
		try {
			client.putAsync(null, path, null, null, Entity.entity(stringEnitiy, MediaType.TEXT_PLAIN_TYPE));
			Assert.assertTrue(false);
		} catch(Exception e) {
			Assert.assertTrue(true);
		}
		
		String result = client.put("http://localhost:" + server.getLocalPort(), path, null, null, Entity.entity(stringEnitiy, MediaType.TEXT_PLAIN_TYPE));
		Assert.assertTrue(result.contains(path));
		Assert.assertTrue(result.endsWith("PUT"));
		Assert.assertTrue(result.contains(stringEnitiy));
	}
	
	@AfterClass
	public static void afterClass() {
		factory.close();
		server.shutdown();
	}
}
