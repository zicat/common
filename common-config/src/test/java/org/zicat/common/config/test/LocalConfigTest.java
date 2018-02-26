package org.zicat.common.config.test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.Test;

import org.zicat.common.config.Config;
import org.zicat.common.config.LocalConfig;
import org.zicat.common.config.schema.InputStreamSchema;
import org.zicat.common.config.schema.InputStreamSchemaFactory;

import junit.framework.Assert;

/**
 * 
 * @author lz31
 *
 */
public class LocalConfigTest {
	
	private static final InputStreamSchema<Properties> PROPERTIES_SCHEMA = InputStreamSchemaFactory.createPropertiesSchema(StandardCharsets.UTF_8);
	private static final InputStreamSchema<AA> AA_JSON_SCHEMA = InputStreamSchemaFactory.createGsonSchema(AA.class);
	private static final InputStreamSchema<AA> AA_XML_SCHEMA = InputStreamSchemaFactory.createJAXBSchema(AA.class);
	
	
	@Test
	public void testProperties() throws Exception {
		
		Config<URL, Properties> localConfig = new LocalConfig<>("aa.properties", PROPERTIES_SCHEMA);
		Properties properties = localConfig.newInstance();
		Assert.assertTrue(properties.getProperty("aa").contains("张"));
		Assert.assertEquals(properties, localConfig.getInstance());
	}

	@Test
	public void testXml() throws Exception {

		Config<URL, AA> localConfig = new LocalConfig<>("aa.xml", AA_XML_SCHEMA);
		AA aa = localConfig.newInstance();
		Assert.assertTrue(aa.getName().contains("张"));
		Assert.assertEquals(aa, localConfig.getInstance());
	}
	
	@Test
	public void testJson() throws Exception {
		
		LocalConfig<AA> jsonConfig= new LocalConfig<>("aa.json", AA_JSON_SCHEMA);
		AA aa = jsonConfig.newInstance();
		Assert.assertTrue(aa.getName().contains("张"));
		Assert.assertEquals(aa, jsonConfig.getInstance());
		Assert.assertNotSame(aa, jsonConfig.newInstance());
	}
}
