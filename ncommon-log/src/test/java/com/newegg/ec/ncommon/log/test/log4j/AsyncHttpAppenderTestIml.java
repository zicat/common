package com.newegg.ec.ncommon.log.test.log4j;

import java.util.List;

import org.apache.log4j.spi.LoggingEvent;

import com.newegg.ec.ncommon.http.client.RestfullClient;
import com.newegg.ec.ncommon.log.log4j.http.AsyncHttpAppender;

import junit.framework.Assert;

public class AsyncHttpAppenderTestIml extends AsyncHttpAppender {

	@Override
	public void consume(RestfullClient client, List<LoggingEvent> elements) throws Exception {
		Assert.assertNotNull(client);
	}

	@Override
	public void dealException(RestfullClient client, List<LoggingEvent> elements, Exception e) {
		Assert.assertNotNull(client);
		
	}
}
