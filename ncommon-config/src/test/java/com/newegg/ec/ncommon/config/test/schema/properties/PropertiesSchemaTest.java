package com.newegg.ec.ncommon.config.test.schema.properties;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.newegg.ec.ncommon.config.schema.properties.PropertiesSchema;
import com.newegg.ec.ncommon.utils.io.IOUtils;

import junit.framework.Assert;

/**
 * 
 * @author lz31
 *
 */
public class PropertiesSchemaTest {
	
	public static String dir = null;
	public static PropertiesSchema schema = new PropertiesSchema(StandardCharsets.UTF_8);
	
	@BeforeClass
	public static void before() {
		String packageName = PropertiesSchemaTest.class.getPackage().getName();
		dir = packageName.replace(".", "/") + "/";
	}
	
	@Test
	public void test() throws Exception {
		
		InputStream aaSource = null;
		InputStream aaSource2 = null;
		try {
			aaSource = Thread.currentThread().getContextClassLoader().getResourceAsStream(dir + "aa.properties");
			aaSource2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(dir + "aa2.properties");
			Properties aa = schema.unmarshal(aaSource);
			Properties aa2 = schema.unmarshal(aa, aaSource2);
			Assert.assertEquals(aa2.getProperty("a"), "2,4");
			Assert.assertEquals(aa2.getProperty("b"), "10");
			Assert.assertEquals(aa2.getProperty("c"), "3");
			Assert.assertEquals(aa.getProperty("a"), "1");
			Assert.assertEquals(aa.getProperty("b"), "10");
			Assert.assertEquals(aa.getProperty("c"), null);
		} finally {
			IOUtils.closeQuietly(aaSource, aaSource2);
		}
	}
}
