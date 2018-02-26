package org.zicat.common.config.test.schema.json;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import org.zicat.common.config.schema.json.GsonSchema;
import org.zicat.common.config.test.AA;
import org.zicat.common.utils.io.IOUtils;

import junit.framework.Assert;

/**
 * 
 * @author lz31
 *
 */
public class GsonSchemaTest {
	
	public static GsonSchema<AA> schema = new GsonSchema<>(AA.class, null, StandardCharsets.UTF_8);
	
	@Test
	public void test() throws Exception {
		
		InputStream aaSource = null;
		try {
			aaSource = Thread.currentThread().getContextClassLoader().getResourceAsStream("schema-aa.json");
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
