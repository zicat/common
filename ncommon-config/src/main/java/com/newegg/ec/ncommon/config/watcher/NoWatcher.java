package com.newegg.ec.ncommon.config.watcher;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.newegg.ec.ncommon.config.AbstractConfig;
import com.newegg.ec.ncommon.config.listener.AbstractConfigListener;
import com.newegg.ec.ncommon.config.listener.LoggerConfigListener;

/**
 * do not care changed and never callback listener
 * 
 * @author lz31
 *
 * @param <T>
 */
public class NoWatcher<T extends AbstractConfig<?, ?>> implements Watcher<T> {

	protected final Map<T, AbstractConfigListener<T>> container = new ConcurrentHashMap<>();
	protected final AbstractConfigListener<T> DEFAULT_LISTENER = new LoggerConfigListener<>();

	protected final ReentrantReadWriteLock containerLock = new ReentrantReadWriteLock(true);
	protected final ReadLock readLock = containerLock.readLock();
	protected final WriteLock writeLock = containerLock.writeLock();

	public NoWatcher() {
	}

	@Override
	public void register(T config, AbstractConfigListener<T> listener) throws Exception {

		checkClosed();

		if (config == null)
			throw new NullPointerException("config is null");

		if (container.containsKey(config))
			return;

		writeLock.lock();
		try {
			if (container.containsKey(config))
				return;

			if (listener == null)
				listener = DEFAULT_LISTENER;

			config.newInstance();
			container.put(config, listener);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void register(T config) throws Exception {
		register(config, null);
	}

	@Override
	public void unregister(T config) throws Exception {

		if (config == null)
			throw new NullPointerException("config is null");

		checkClosed();
		writeLock.lock();
		try {
			container.remove(config);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void close() throws IOException {
	}

	/**
	 * 
	 */
	protected void checkClosed() {

	}
}
