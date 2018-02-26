package org.zicat.common.config.test.schema.properties;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import org.zicat.common.config.schema.properties.PropertiesSchema;
import org.zicat.common.utils.io.IOUtils;

import junit.framework.Assert;

/**
 * 
 * @author lz31
 *
 */
public class PropertiesSchemaTest {
	
	public static PropertiesSchema schema = new PropertiesSchema(StandardCharsets.UTF_8);
	
	@Test
	public void test() throws Exception {
		
		InputStream aaSource = null;
		InputStream aaSource2 = null;
		try {
			aaSource = Thread.currentThread().getContextClassLoader().getResourceAsStream("schema-aa.properties");
			aaSource2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("schema-aa2.properties");
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
