package com.newegg.ec.ncommon.config.watcher;

import java.io.Closeable;
import java.io.IOException;

import com.newegg.ec.ncommon.config.AbstractConfig;
import com.newegg.ec.ncommon.config.listener.AbstractConfigListener;

/**
 * 
 * @author lz31
 * @ThreadSafe
 * @param <C>
 */
public interface Watcher<C extends AbstractConfig<?, ?>> extends Closeable {
	
	/**
	 * 
	 * @param config
	 * @param listener
	 * @throws Exception
	 */
	void register(C config, AbstractConfigListener<C> listener) throws Exception;
	
	/**
	 * 
	 * @param config
	 * @throws Exception
	 */
	void register(C config) throws Exception; 
	
	/**
	 * 
	 * @param config
	 * @throws Exception
	 */
	void unregister(C config) throws Exception;
	
	/**
	 * 
	 */
	void close() throws IOException;
}
