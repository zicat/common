package com.newegg.ec.ncommon.config.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newegg.ec.ncommon.config.AbstractConfig;

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
