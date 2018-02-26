package org.zicat.common.config.test.watcher.local;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Test;

import org.zicat.common.config.LocalConfig;
import org.zicat.common.config.listener.AbstractConfigListener;
import org.zicat.common.config.schema.InputStreamSchema;
import org.zicat.common.config.schema.InputStreamSchemaFactory;
import org.zicat.common.config.test.watcher.FileUtils;
import org.zicat.common.config.test.watcher.LoggerConfigListener2;
import org.zicat.common.config.watcher.Watcher;
import org.zicat.common.config.watcher.local.LocalWatcher;

import junit.framework.Assert;

/**
 * 
 * @author lz31
 *
 */
public class LocalWatcherTest {
	
	private static final InputStreamSchema<Properties> PROPERTIES_SCHEMA = InputStreamSchemaFactory.createPropertiesSchema(StandardCharsets.UTF_8);
	private static final AbstractConfigListener<LocalConfig<?>> listener = new LoggerConfigListener2<>();
	
	private static final File DIR = new File("aaXml").getParentFile();
	private static final File PATH1 = new File(DIR, "bb1.properties");
	private static final File PATH2 = new File(DIR, "bb2.properties");
	private static final File PATH3 = new File(DIR, "bb3.properties");
	private static final File PATH4 = new File(DIR, "bb4.properties");
	private static final File PATH5 = new File(DIR, "bb5.properties");
	private static final File PATH6 = new File(DIR, "bb6.properties");
	
	private static final Watcher<LocalConfig<?>> watcher = new LocalWatcher();
	private static LocalConfig<Properties> CONFIG1 = null;
	private static LocalConfig<Properties> CONFIG2 = null;
	private static LocalConfig<Properties> CONFIG3 = null;
	private static LocalConfig<Properties> CONFIG4 = null;
	private static LocalConfig<Properties> CONFIG5 = null;
	private static LocalConfig<Properties> CONFIG6 = null;
	
	static {
		try {
			FileUtils.createNewFile(PATH1, new String("").getBytes());
			FileUtils.createNewFile(PATH2, new String("").getBytes());
			FileUtils.createNewFile(PATH3, new String("").getBytes());
			FileUtils.createNewFile(PATH4, new String("").getBytes());
			FileUtils.createNewFile(PATH5, new String("").getBytes());
			FileUtils.createNewFile(PATH6, new String("").getBytes());
			
			/**
			 *  1------>2--------->4---------->6
			 *          |
			 *          |
			 *  	    3--------->5 
			 * 
			 */
			CONFIG1 = new LocalConfig<>(PATH1.getPath(), PROPERTIES_SCHEMA);
			CONFIG2 = new LocalConfig<>(PATH2.getPath(), PROPERTIES_SCHEMA, CONFIG1);
			CONFIG3 = new LocalConfig<>(PATH3.getPath(), PROPERTIES_SCHEMA, CONFIG1);
			CONFIG4 = new LocalConfig<>(PATH4.getPath(), PROPERTIES_SCHEMA, CONFIG2);
			CONFIG5 = new LocalConfig<>(PATH5.getPath(), PROPERTIES_SCHEMA, CONFIG3);
			CONFIG6 = new LocalConfig<>(PATH6.getPath(), PROPERTIES_SCHEMA, CONFIG4);
			
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
		FileUtils.createNewFile(PATH1, new String("aa=111").getBytes());
		Thread.sleep(50);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "111");
		
		FileUtils.createNewFile(PATH4, new String("aa=222").getBytes());
		Thread.sleep(50);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "222");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "111");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "222");
		
		FileUtils.createNewFile(PATH1, new String("aa=333").getBytes());
		Thread.sleep(50);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "222");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "222");
		
		FileUtils.createNewFile(PATH4, new String("").getBytes());
		Thread.sleep(50);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "333");
		
		watcher.unregister(CONFIG4);
		FileUtils.createNewFile(PATH4, new String("aa=rr").getBytes());
		Thread.sleep(200);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "333");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "333");
		
		FileUtils.createNewFile(PATH1, new String("aa=444").getBytes());
		Thread.sleep(200);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "rr");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "rr");
		
		
		watcher.unregister(CONFIG1);
		FileUtils.createNewFile(PATH1, new String("aa=pp").getBytes());
		Thread.sleep(200);
		Assert.assertEquals(CONFIG1.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG2.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG3.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG4.getInstance().getProperty("aa"), "rr");
		Assert.assertEquals(CONFIG5.getInstance().getProperty("aa"), "444");
		Assert.assertEquals(CONFIG6.getInstance().getProperty("aa"), "rr");
		
		FileUtils.createNewFile(PATH6, new String("aa=555").getBytes());
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
		watcher.close();
		PATH1.delete();
		PATH2.delete();
		PATH3.delete();
		PATH4.delete();
		PATH5.delete();
		PATH6.delete();
	}
}
