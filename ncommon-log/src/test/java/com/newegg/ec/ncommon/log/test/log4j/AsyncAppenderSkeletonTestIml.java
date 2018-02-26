package com.newegg.ec.ncommon.log.test.log4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.spi.LoggingEvent;

import com.newegg.ec.ncommon.log.log4j.AsyncAppenderSkeleton;

public class AsyncAppenderSkeletonTestIml extends AsyncAppenderSkeleton {
	
	public static AtomicInteger integer = new AtomicInteger(0);
	
	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	public void consume(List<LoggingEvent> elements) throws Exception {
		integer.incrementAndGet();
		for(LoggingEvent event: elements) {
			System.out.println(event.getMessage());
		}
	}

	@Override
	public void dealException(List<LoggingEvent> elements, Exception e) {
		throw new RuntimeException("consumer failed", e);
	}
}
