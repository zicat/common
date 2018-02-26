package com.newegg.ec.ncommon.log.test.log4j;

import org.apache.log4j.Logger;
import org.junit.Test;

import junit.framework.Assert;

public class AsyncAppenderSkeletonTest {
	
	@Test
	public void test() throws InterruptedException {
		
		Logger LOG = Logger.getLogger(AsyncAppenderSkeletonTest.class);
		LOG.error("mail testing1");
		LOG.error("mail testing1");
		LOG.error("mail testing1");
		LOG.error("mail testing1");
		LOG.error("mail testing1");
		LOG.error("mail testing1");
		LOG.error("mail testing1");
		LOG.error("mail testing2");
		LOG.error("mail testing2");
		LOG.error("mail testing2");
		LOG.error("mail testing2");
		LOG.error("mail testing2");
		LOG.error("mail testing2");
		LOG.error("mail testing2");
		LOG.error("mail testing2");
		LOG.error("mail testing2");
		LOG.error("mail testing2");
		LOG.error("mail testing2");
		LOG.error("mail testing2");
		LOG.info("testing");
		Thread.sleep(200);
		Assert.assertTrue(AsyncAppenderSkeletonTestIml.integer.get() > 0);
		Thread.sleep(2000);
	}
}
