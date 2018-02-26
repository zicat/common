package com.newegg.ec.ncommon.config.test.schema.xml;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.newegg.ec.ncommon.config.schema.xml.JAXBSchema;
import com.newegg.ec.ncommon.config.test.AA;
import com.newegg.ec.ncommon.utils.io.IOUtils;

import junit.framework.Assert;

/**
 * 
 * @author lz31
 *
 */
public class JAXBSchemaTest {
	
	public static String dir = null;
	public static JAXBSchema<AA> schema = new JAXBSchema<>(AA.class, StandardCharsets.UTF_8);
	
	@BeforeClass
	public static void before() {
		
		String packageName = JAXBSchemaTest.class.getPackage().getName();
		dir = packageName.replace(".", "/") + "/";
	}
	
	@Test
	public void test() throws Exception {
		
		InputStream aaSource = null;
		try {
			aaSource = Thread.currentThread().getContextClassLoader().getResourceAsStream(dir + "aa.xml");
			AA aa = schema.unmarshal(aaSource);
			Assert.assertEquals(aa.getName(), "333");
			try {
				schema.unmarshal(aa, aaSource);
				Assert.assertTrue(false);
			} catch(UnsupportedOperationException e) {
				Assert.assertTrue(true);
			}
		} finally {
			IOUtils.closeQuietly(aaSource);
		}
	}
}
