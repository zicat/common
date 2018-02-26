package org.zicat.common.http.client.test;

import org.zicat.common.http.client.RestfullClient;
import org.zicat.common.http.client.factory.AbstractClientFactory;
import org.zicat.common.http.client.factory.jersey.DefaultJerseyClientFactory;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by lz31 on 2017/9/21.
 */
public class RestfullClientTest {

    private static AbstractClientFactory factory = new DefaultJerseyClientFactory(10, 1000, 1000);
    private static HttpEmbeddedServer server = new HttpEmbeddedServer();

    @BeforeClass
    public static void allBefore() throws IOException, InterruptedException {

        final Map<String, HttpRequestHandler> registerHandlerList = new HashMap<>();
        registerHandlerList.put("/*", new EchoHandler());
        server.buildAndStart(registerHandlerList);
    }

    @Test
    public void testPut() throws Exception {

        RestfullClient client = new RestfullClient(factory);
        String path = "/echo/cc";

        String stringEnitiy = new String("string entity");

        try {
            client.putAsync(null, path, null, null, Entity.entity(stringEnitiy, MediaType.TEXT_PLAIN_TYPE));
            Assert.assertTrue(false);
        } catch(Exception e) {
            Assert.assertTrue(true);
        }

        Future<String> resultFuture = client.putAsync("http://localhost:" + server.getLocalPort(), path, null, null, Entity.entity(stringEnitiy, MediaType.TEXT_PLAIN_TYPE));
        String result = client.put("http://localhost:" + server.getLocalPort(), path, null, null, Entity.entity(stringEnitiy, MediaType.TEXT_PLAIN_TYPE));
        Assert.assertTrue(result.contains(path));
        Assert.assertTrue(result.endsWith("PUT"));
        Assert.assertTrue(result.contains(stringEnitiy));

        Assert.assertTrue(resultFuture.get().contains(path));
        Assert.assertTrue(resultFuture.get().endsWith("PUT"));
        Assert.assertTrue(resultFuture.get().contains(stringEnitiy));
    }

    @Test
    public void testPost() throws Exception {

        RestfullClient client = new RestfullClient(factory);
        String path = "/echo/cc";

        String stringEnitiy = new String("string entity");

        Future<String> resultFuture = client.postAsync("http://localhost:" + server.getLocalPort(), path, null, null, Entity.entity(stringEnitiy, MediaType.TEXT_PLAIN_TYPE));
        String result = client.post("http://localhost:" + server.getLocalPort(), path, null, null, Entity.entity(stringEnitiy, MediaType.TEXT_PLAIN_TYPE));
        Assert.assertTrue(result.contains(path));
        Assert.assertTrue(result.endsWith("POST"));
        Assert.assertTrue(result.contains(stringEnitiy));

        Assert.assertTrue(resultFuture.get().contains(path));
        Assert.assertTrue(resultFuture.get().endsWith("POST"));
        Assert.assertTrue(resultFuture.get().contains(stringEnitiy));
    }

    @Test
    public void testGet() throws Exception {

        RestfullClient client = new RestfullClient(factory);
        String path = "/echo/bb";

        Future<String> resultFuture = client.getAsync("http://localhost:" + server.getLocalPort(), path, null, null);
        Future<String> resultFuture2 = client.getAsync("http://localhost:" + server.getLocalPort(), path, null, null);
        String result = client.get("http://localhost:" + server.getLocalPort(), path, null, null);
        Assert.assertTrue(result.equals(resultFuture2.get()));
        Assert.assertTrue(result.endsWith("GET"));
        Assert.assertTrue(result.contains(path));
        Assert.assertTrue(result.equals(resultFuture.get()));



        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        List<Object> values = new ArrayList<>();
        values.add("bbb");
        values.add("ccc");
        headers.put("aaa", values);

        MultivaluedMap<String, Object> params = new MultivaluedHashMap<>();
        values = new ArrayList<>();
        values.add("qqq");
        values.add("rrr");
        params.put("ppp", values);

        resultFuture = client.getAsync("http://localhost:" + server.getLocalPort(), path, headers, params);
        result = client.get("http://localhost:" + server.getLocalPort(), path, headers, params);
        Assert.assertTrue(result.contains(path + "?ppp=qqq&ppp=rrr"));
        Assert.assertTrue(result.endsWith("GET"));
        Assert.assertTrue(result.contains("aaa:bbb,ccc"));
        Assert.assertTrue(result.equals(resultFuture.get()));

        resultFuture = client.getAsync("http://localhost:" + server.getLocalPort(), null, headers, params);
        result = client.get("http://localhost:" + server.getLocalPort(), null, headers, params);
        Assert.assertTrue(result.contains("?ppp=qqq&ppp=rrr"));
        Assert.assertTrue(result.contains("aaa:bbb,ccc"));
        Assert.assertTrue(result.endsWith("GET"));
        Assert.assertTrue(result.equals(resultFuture.get()));
    }

    @AfterClass
    public static void allAfter() {
    	factory.close();
        server.shutdown();
    }
}