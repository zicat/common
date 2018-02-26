package org.zicat.common.log.log4j.mail;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * @author lz31
 *
 */
public class AsyncReducedSMTPAppender extends AsyncSMTPAppender {
	
	@Override
	protected List<LoggingEvent> reduce(List<LoggingEvent> elements) {
		
		List<LoggingEvent> result = new ArrayList<LoggingEvent>();
		for(LoggingEvent event: elements) {
			boolean contains = false;
			for(LoggingEvent resultEvent: result) {
				if(resultEvent.getMessage().equals(event.getMessage())) {
					contains = true;
					break;
				}
			}
			if(!contains) {
				result.add(event);
			}
		}
		return result;
	}
}
