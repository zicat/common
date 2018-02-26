package com.newegg.ec.ncommon.config.test.schema.json;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.newegg.ec.ncommon.config.schema.json.GsonSchema;
import com.newegg.ec.ncommon.config.test.AA;
import com.newegg.ec.ncommon.utils.io.IOUtils;

import junit.framework.Assert;

/**
 * 
 * @author lz31
 *
 */
public class GsonSchemaTest {
	
	public static String dir = null;
	public static GsonSchema<AA> schema = new GsonSchema<>(AA.class, null, StandardCharsets.UTF_8);
	
	@BeforeClass
	public static void before() {
		String packageName = GsonSchemaTest.class.getPackage().getName();
		dir = packageName.replace(".", "/") + "/";
	}
	
	@Test
	public void test() throws Exception {
		
		InputStream aaSource = null;
		try {
			aaSource = Thread.currentThread().getContextClassLoader().getResourceAsStream(dir + "aa.json");
			AA aa = schema.unmarshal(aaSource);
			Assert.assertEquals(aa.getName(), "123");
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
