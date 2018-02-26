package org.zicat.common.log.log4j.mail;

import java.util.List;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.spi.LoggingEvent;

import org.zicat.common.log.log4j.AsyncAppenderSkeleton;
import org.zicat.common.utils.dp.ConsumerHandler;

/**
 * 
 * @author lz31
 *
 */
public class AsyncSMTPAppender extends SMTPAppender implements ConsumerHandler<LoggingEvent> {

	private String filePath;
	private int segmentSize = 1024 * 1024 * 1;
	private int consumerMaxIntervalTimeMillis = 5000;
	private int consumerMaxCount = 50;
	private AsyncAppenderSkeleton wrapper = null;
	private Object lock = new Object();

	@Override
	public void activateOptions() {

		super.activateOptions();
		final AsyncSMTPAppender thisInstance = this;
		wrapper = new AsyncAppenderSkeleton() {

			@Override
			public boolean requiresLayout() {
				return thisInstance.requiresLayout();
			}

			@Override
			public void dealException(List<LoggingEvent> elements, Exception e) {
				thisInstance.dealException(elements, e);

			}

			@Override
			public void consume(List<LoggingEvent> elements) throws Exception {
				thisInstance.consume(elements);
			}
		};
		wrapper.setFilePath(filePath);
		wrapper.setSegmentSize(segmentSize);
		wrapper.setConsumerMaxIntervalTimeMillis(consumerMaxIntervalTimeMillis);
		wrapper.setConsumerMaxCount(consumerMaxCount);
		wrapper.setThreadCount(1);
		wrapper.activateOptions();
	}

	@Override
	public void append(LoggingEvent event) {
		wrapper.append(event);
	}
	
	@Override
	public void close() {
		try {
			wrapper.close();
		} finally {
			super.close();
		}
	}

	@Override
	public void consume(List<LoggingEvent> elements) throws Exception {

		elements = reduce(elements);
		for (LoggingEvent event : elements) {
			synchronized (lock) { // mail append not thread safe
				super.append(event);
			}
		}
	}

	/**
	 * reduce same mail
	 * 
	 * @param elements
	 */
	protected List<LoggingEvent> reduce(List<LoggingEvent> elements) {
		return elements;
	}

	@Override
	public void dealException(List<LoggingEvent> elements, Exception e) {
		LogLog.error("Error occured while sending e-mail notification.", e);
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getSegmentSize() {
		return segmentSize;
	}

	public void setSegmentSize(int segmentSize) {
		this.segmentSize = segmentSize;
	}

	public int getConsumerMaxIntervalTimeMillis() {
		return consumerMaxIntervalTimeMillis;
	}

	public void setConsumerMaxIntervalTimeMillis(int consumerMaxIntervalTimeMillis) {
		this.consumerMaxIntervalTimeMillis = consumerMaxIntervalTimeMillis;
	}

	public int getConsumerMaxCount() {
		return consumerMaxCount;
	}

	public void setConsumerMaxCount(int consumerMaxCount) {
		this.consumerMaxCount = consumerMaxCount;
	}
}
