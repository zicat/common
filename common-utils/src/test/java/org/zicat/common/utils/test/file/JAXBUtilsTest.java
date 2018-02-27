package org.zicat.common.utils.test.file;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import org.zicat.common.utils.file.JAXBUtils;

/**
 * 
 * @author lz31
 *
 */
public class JAXBUtilsTest {
	
	@Test
	public void test() throws IOException, JAXBException {
		
		String path = Thread.currentThread().getContextClassLoader().getResource("aa.xml").getPath();
		AAConfig config = JAXBUtils.unmarshal(path , AAConfig.class);
		Assert.assertTrue(config.getDevelopersConfig().getDeveloperConfig().get(0).getNameConfig().getValue().equals("lyn"));
		config = JAXBUtils.unmarshal(Thread.currentThread().getContextClassLoader().getResourceAsStream("aa.xml"), AAConfig.class);
		Assert.assertTrue(config.getDevelopersConfig().getDeveloperConfig().get(0).getNameConfig().getValue().equals("lyn"));
	}
}
