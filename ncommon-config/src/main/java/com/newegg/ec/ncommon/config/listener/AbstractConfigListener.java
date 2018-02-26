package com.newegg.ec.ncommon.config.listener;

import com.newegg.ec.ncommon.config.AbstractConfig;

/**
 * 
 * @author lz31
 * @ThreadSafe
 * @param <T>
 */
public interface AbstractConfigListener<C extends AbstractConfig<?, ?>> {
	
	/**
	 * 
	 * @param config
	 * @throws Exception
	 */
	void onModify(C config) throws Exception;
}
