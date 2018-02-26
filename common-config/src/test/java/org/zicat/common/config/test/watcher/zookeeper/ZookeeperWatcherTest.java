package org.zicat.common.config.test.watcher.zookeeper;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Test;

import org.zicat.common.config.ZookeeperConfig;
import org.zicat.common.config.dao.zookeeper.CuratorZookeeperClient;
import org.zicat.common.config.dao.zookeeper.ZookeeperClient;
import org.zicat.common.config.listener.AbstractConfigListener;
import org.zicat.common.config.schema.InputStreamSchema;
import org.zicat.common.config.schema.InputStreamSchemaFactory;
import org.zicat.common.config.test.ZookeeperServer;
import org.zicat.common.config.test.watcher.LoggerConfigListener2;
import org.zicat.common.config.watcher.Watcher;
import org.zicat.common.config.watcher.zookeeper.ZookeeperWatcher;

import junit.framework.Assert;

/**
 * 
 * @author lz31
 *
 */
public class ZookeeperWatcherTest {
	
	private static final InputStreamSchema<Properties> PROPERTIES_SCHEMA = InputStreamSchemaFactory.createPropertiesSchema(StandardCharsets.UTF_8);
	private static final AbstractConfigListener<ZookeeperConfig<?>> listener = new LoggerConfigListener2<>();
	private static final String PATH1 = "/test/aa";
	private static final String PATH2 = "/test/aa/bb2";
	private static final String PATH3 = "/test/aacc";
	private static final String PATH4 = "/test/aa/aa";
	private static final String PATH5 = "/test/ee";
	private static final String PATH6 = "/hh";
	private static final ZookeeperServer server = new ZookeeperServer();
	private static ZookeeperClient client;
	
	private static final Watcher<ZookeeperConfig<?>> watcher = new ZookeeperWatcher();
	private static ZookeeperConfig<Properties> CONFIG1 = null;
	private static ZookeeperConfig<Properties> CONFIG2 = null;
	private static ZookeeperConfig<Properties> CONFIG3 = null;
	private static ZookeeperConfig<Properties> CONFIG4 = null;
	private static ZookeeperConfig<Properties> CONFIG5 = null;
	private static ZookeeperConfig<Properties> CONFIG6 = null;
	
	static {
		try {
			server.initZK(); //start local zookeeper
			client = new CuratorZookeeperClient(server.getHostPort(), 2000);
			client.createNode(PATH1, new String("").getBytes());
			client.createNode(PATH2, new String("").getBytes());
			client.createNode(PATH3, new String("").getBytes());
			client.createNode(PATH4, new String("").getBytes());
			client.createNode(PATH5, new String("").getBytes());
			client.createNode(PATH6, new String("").getBytes());
			
			/**
			 *  1------>2--------->4---------->6
			 *          |
			 *          |
			 *  	    3--------->5 
			 * 
			 */
			CONFIG1 = new ZookeeperConfig<>(server.getHostPort(), PATH1, 2000, PROPERTIES_SCHEMA);
			CONFIG2 = new ZookeeperConfig<>(server.getHostPort(), PATH2, 2000, PROPERTIES_SCHEMA, CONFIG1);
			CONFIG3 = new ZookeeperConfig<>(server.getHostPort(), PATH3, 2000, PROPERTIES_SCHEMA, CONFIG1);
			CONFIG4 = new ZookeeperConfig<>(server.getHostPort(), PATH4, 2000, PROPERTIES_SCHEMA, CONFIG2);
			CONFIG5 = new ZookeeperConfig<>(server.getHostPort(), PATH5, 2000, PROPERTIES_SCHEMA, CONFIG3);
			CONFIG6 = new ZookeeperConfig<>(server.getHostPort(), PATH6, 2000, PROPERTIES_SCHEMA, CONFIG4);
			
			watcher.register(CONFIG1, listener);
			watcher.register(CONFIG2, listener);
			watcher.register(CONFIG3, listener);
			watcher.register(CONFIG4, listener);
			watcher.register(CONFIG5, listener);
			watcher.register(CONFIG6, listener);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void test() throws Exception {
		
		Assert.assertNotNull(CONFIG1.getInstance());
		client.setData(PATH1,  new String("aa=111").getBytes());
		Thread.sleep(100);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "111");
		
		client.setData(PATH4,  new String("aa=222").getBytes());
		Thread.sleep(100);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "222");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "222");
		
		client.setData(PATH1,  new String("aa=333").getBytes());
		Thread.sleep(100);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "222");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "222");
		
		client.setData(PATH4,  new String("").getBytes());
		Thread.sleep(100);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "333");
		
		watcher.unregister(CONFIG4);
		client.setData(PATH4,  new String("aa=rr").getBytes());
		Thread.sleep(200);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "333");
		
		client.setData(PATH1,  new String("aa=444").getBytes());
		Thread.sleep(200);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "rr");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "rr");
		
		
		watcher.unregister(CONFIG1);
		client.setData(PATH1,  new String("aa=pp").getBytes());
		Thread.sleep(200);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "rr");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "rr");
		
		client.setData(PATH6,  new String("aa=555").getBytes());
		Thread.sleep(200);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "rr");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "555");
	}
	
	@AfterClass
	public static void after() throws Exception {
		watcher.unregister(CONFIG1);
		watcher.close();
		server.closeZK();
	}
}
