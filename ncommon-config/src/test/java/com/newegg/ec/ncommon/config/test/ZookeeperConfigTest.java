package com.newegg.ec.ncommon.config.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.newegg.ec.ncommon.config.ZookeeperConfig;
import com.newegg.ec.ncommon.config.dao.zookeeper.CuratorZookeeperClient;
import com.newegg.ec.ncommon.config.dao.zookeeper.ZookeeperClient;
import com.newegg.ec.ncommon.config.schema.InputStreamSchema;
import com.newegg.ec.ncommon.config.schema.InputStreamSchemaFactory;

import junit.framework.Assert;

/**
 * 
 * @author lz31
 *
 */
public class ZookeeperConfigTest {
	
	private static final Gson GSON = new Gson();
	private static final ZookeeperServer server = new ZookeeperServer();
	private static final InputStreamSchema<AA> AA_JSON_SCHEMA = InputStreamSchemaFactory.createGsonSchema(AA.class);
	private static final String zookeeperPath = "/cf/aa";
	
	@BeforeClass
	public static void before() throws Exception {
		
		server.initZK();
		AA aa = new AA("testing");
		ZookeeperClient client = new CuratorZookeeperClient(server.getHostPort(), 2000);
		client.createNode(zookeeperPath, GSON.toJson(aa).getBytes(StandardCharsets.UTF_8));
	}
	
	@Test
	public void test() throws Exception {
		ZookeeperConfig<AA> zookeeperConfig = new ZookeeperConfig<>(server.getHostPort(), zookeeperPath, 2000, AA_JSON_SCHEMA);
		AA aa = zookeeperConfig.newInstance();
		Assert.assertEquals(aa.getName(), "testing");
		Assert.assertEquals(aa, zookeeperConfig.getInstance());
	}
	
	@AfterClass
	public static void after() throws IOException {
		server.closeZK();
	}
}
