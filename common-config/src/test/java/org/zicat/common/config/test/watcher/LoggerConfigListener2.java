package org.zicat.common.config.test.watcher;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zicat.common.config.AbstractConfig;
import org.zicat.common.config.listener.AbstractConfigListener;
import org.zicat.common.config.listener.LoggerConfigListener;

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
