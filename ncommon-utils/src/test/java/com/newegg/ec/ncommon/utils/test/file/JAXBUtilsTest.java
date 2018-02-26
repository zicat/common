package com.newegg.ec.ncommon.utils.test.file;

import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import com.newegg.ec.ncommon.utils.file.JAXBUtils;

/**
 * 
 * @author lz31
 *
 */
public class JAXBUtilsTest {
	
	@Test
	public void test() throws IOException, JAXBException {
		
		try {
			JAXBUtils.unmarshal("aa.xml", AAConfig.class);
			Assert.assertTrue(false);
		} catch(Exception e) {
			Assert.assertTrue(true);
		}
		
		AAConfig config = JAXBUtils.unmarshal(JAXBUtilsTest.class.getPackage().getName().replace(".", "/") + "/aa.xml" , AAConfig.class);
		Assert.assertTrue(config.getDevelopersConfig().getDeveloperConfig().get(0).getNameConfig().getValue().equals("lyn"));
		
		URL url = Thread.currentThread().getContextClassLoader().getResource(JAXBUtilsTest.class.getPackage().getName().replace(".", "/") + "/aa.xml");
		config = JAXBUtils.unmarshal(url.getFile() , AAConfig.class);
		Assert.assertTrue(config.getDevelopersConfig().getDeveloperConfig().get(0).getNameConfig().getValue().equals("lyn"));
	}
}
