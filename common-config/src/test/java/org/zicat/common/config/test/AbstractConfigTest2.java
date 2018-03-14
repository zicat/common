package org.zicat.common.config.test;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.Test;
import org.zicat.common.config.LocalConfig;
import org.zicat.common.config.ZookeeperConfig;
import org.zicat.common.config.dao.zookeeper.CuratorZookeeperClient;
import org.zicat.common.config.dao.zookeeper.ZookeeperClient;
import org.zicat.common.config.listener.AbstractConfigListener;
import org.zicat.common.config.schema.InputStreamSchema;
import org.zicat.common.config.schema.InputStreamSchemaFactory;
import org.zicat.common.config.test.watcher.FileUtils;
import org.zicat.common.config.watcher.Watcher;
import org.zicat.common.config.watcher.zookeeper.ZookeeperWatcher;
import org.zicat.common.utils.io.IOUtils;

import junit.framework.Assert;

/**
 * 
 * @author zicat
 *
 */
public class AbstractConfigTest2 {
	
	private static final InputStreamSchema<Properties> PROPERTIES_SCHEMA = InputStreamSchemaFactory.createPropertiesSchema(StandardCharsets.UTF_8);
	
	@SuppressWarnings("unchecked")
	private static final AbstractConfigListener<ZookeeperConfig<?>> listener = config -> {
		
		LocalConfig<Properties> localConfig = (LocalConfig<Properties>) config.getParentConfig();
		ZookeeperConfig<Properties> zookeeperConfig = (ZookeeperConfig<Properties>) config;
		Writer fw = null;
		try {
			File file = new File(localConfig.getSource().getFile());
			fw = new FileWriter(file, false);
			zookeeperConfig.getInstance().store(fw, "Flsuh Zookeeper Data to Local");
			fw.flush();
		} finally {
			IOUtils.closeQuietly(fw);
		}
	};
	
	private static final File LOCAL_FILE = new File("aaa2.properties");
	private static final String ZOOKEEPER_PATH = "/test/aaa2.properties";
	private static final ZookeeperServer server = new ZookeeperServer();
	private static ZookeeperClient client;
	
	private static final Watcher<ZookeeperConfig<?>> zookeeperWatcher = new ZookeeperWatcher();
	
	private static LocalConfig<Properties> localConfig;
	private static ZookeeperConfig<Properties> zooKeeperConfig;
	
	static {
		try {
			//init zookeeper data
			server.initZK();
			client = new CuratorZookeeperClient(server.getHostPort(), 2000);
			client.createNode(ZOOKEEPER_PATH, new String("").getBytes());
			
			//init local data
			FileUtils.createNewFile(LOCAL_FILE, new String("").getBytes());
			
			//set local config as parent config of zookeeperconfig
			localConfig = new LocalConfig<>(LOCAL_FILE.getPath(), PROPERTIES_SCHEMA);
			zooKeeperConfig = new ZookeeperConfig<>(server.getHostPort(), ZOOKEEPER_PATH, 3000, PROPERTIES_SCHEMA, localConfig);
			zookeeperWatcher.register(zooKeeperConfig, listener);
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void test() throws Exception {
		
		client.setData(ZOOKEEPER_PATH,  new String("aa=111").getBytes());
		Thread.sleep(100);
		Assert.assertEquals(zooKeeperConfig.getInstance().getProperty("aa"), "111");
		
		zookeeperWatcher.close();
		server.closeZK();
		LOCAL_FILE.delete();
	}
}
