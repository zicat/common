package org.zicat.common.config.watcher;

import java.io.Closeable;
import java.io.IOException;

import org.zicat.common.config.AbstractConfig;
import org.zicat.common.config.listener.AbstractConfigListener;

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
