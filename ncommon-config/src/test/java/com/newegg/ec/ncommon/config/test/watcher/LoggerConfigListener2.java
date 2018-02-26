package com.newegg.ec.ncommon.config.test.watcher;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newegg.ec.ncommon.config.AbstractConfig;
import com.newegg.ec.ncommon.config.listener.AbstractConfigListener;
import com.newegg.ec.ncommon.config.listener.LoggerConfigListener;

/**
 * 
 * @author lz31
 *
 */
public class LoggerConfigListener2<C extends AbstractConfig<?, ?>> implements AbstractConfigListener<C> {
	
	private static final Logger LOG = LoggerFactory.getLogger(LoggerConfigListener.class);
	

	@Override
	public void onModify(C config) throws Exception {
		LOG.info(config.getSource() + " changed!!");
		System.out.println(config.getSource() + " changed!!" + config.getInstance());
	}
}
