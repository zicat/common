package org.zicat.common.log.log4j.mail;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * @author lz31
 *
 */
public class AsyncSubjectReducedSMTPAppender extends AsyncSMTPAppender {

	@Override
	protected List<LoggingEvent> reduce(List<LoggingEvent> elements) {

		List<LoggingEvent> result = new ArrayList<LoggingEvent>();
		if (elements != null && !elements.isEmpty()) // generally elements is never null or empty, but...
			result.add(elements.get(0));
		return result;
	}
}
