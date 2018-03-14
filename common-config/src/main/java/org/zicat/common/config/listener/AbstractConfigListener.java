package org.zicat.common.config.listener;

import org.zicat.common.config.AbstractConfig;

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
	
	/**
	 * only call ones
	 * @param config
	 * @throws Exception
	 */
	void init(C config) throws Exception;
}
