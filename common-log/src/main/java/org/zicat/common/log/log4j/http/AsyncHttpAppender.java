package org.zicat.common.log.log4j.http;

import java.util.List;

import org.apache.log4j.spi.LoggingEvent;

import org.zicat.common.http.client.RestfullClient;
import org.zicat.common.http.client.factory.AbstractClientFactory;
import org.zicat.common.http.client.factory.jersey.GrizzlyJerseyClientFactory;
import org.zicat.common.log.log4j.AsyncAppenderSkeleton;
import org.zicat.common.utils.io.IOUtils;

/**
 * 
 * @author lz31
 *
 */
public abstract class AsyncHttpAppender extends AsyncAppenderSkeleton {

	private int connectionTimeout = 2000;
	private int readTimeout = 4000;

	protected AbstractClientFactory factory = null;

	@Override
	public void activateOptions() {

		super.activateOptions();
		factory = new GrizzlyJerseyClientFactory(getThreadCount(), connectionTimeout, readTimeout);
	}

	@Override
	public void close() {
		try {
			super.close();
		} finally {
			IOUtils.closeQuietly(factory);
			factory = null;
		}
	}

	/**
	 * use client to send http request
	 * 
	 * @param client
	 * @param elements
	 * @throws Exception
	 */
	public abstract void consume(RestfullClient client, List<LoggingEvent> elements) throws Exception;

	/**
	 * deal with Exception, if dealException throw exception, data will be roll back
	 * and consumer again else data will be discard and continue to consumer next
	 * data
	 * 
	 * @param client
	 * @param elements
	 * @param e
	 */
	public abstract void dealException(RestfullClient client, List<LoggingEvent> elements, Exception e);

	@Override
	public final void consume(List<LoggingEvent> elements) throws Exception {
		consume(new RestfullClient(factory), elements);
	}

	@Override
	public final void dealException(List<LoggingEvent> elements, Exception e) {
		dealException(new RestfullClient(factory), elements, e);
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
}
