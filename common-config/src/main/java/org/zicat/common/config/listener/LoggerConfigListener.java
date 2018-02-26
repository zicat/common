package org.zicat.common.config.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zicat.common.config.AbstractConfig;

/**
 * 
 * @author lz31
 * @ThreadSafe
 * @param <T>
 */
public class LoggerConfigListener<C extends AbstractConfig<?, ?>> implements AbstractConfigListener<C> {
	
	private static final Logger LOG = LoggerFactory.getLogger(LoggerConfigListener.class);
	
	@Override
	public void onModify(C config) throws Exception {
		LOG.info(config.getSource() + " changed");
	}
}
