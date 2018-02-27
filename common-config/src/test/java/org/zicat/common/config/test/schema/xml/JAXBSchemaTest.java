package org.zicat.common.config.test.schema.xml;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import org.zicat.common.config.schema.xml.JAXBSchema;
import org.zicat.common.config.test.AA;
import org.zicat.common.utils.io.IOUtils;

import junit.framework.Assert;

/**
 * 
 * @author lz31
 *
 */
public class JAXBSchemaTest {
	
	public static JAXBSchema<AA> schema = new JAXBSchema<>(AA.class, StandardCharsets.UTF_8);

	@Test
	public void test() throws Exception {
		
		InputStream aaSource = null;
		try {
			aaSource = Thread.currentThread().getContextClassLoader().getResourceAsStream( "schema-aa.xml");
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
