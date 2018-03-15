package org.zicat.common.config.watcher;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zicat.common.config.AbstractConfig;
import org.zicat.common.config.listener.AbstractConfigListener;

/**
 * 
 * @author lz31
 * @ThreadSafe
 * @param <T>
 */
public class CycleWatcher<T extends AbstractConfig<?, ?>> extends NoWatcher<T> {

	private final CycleWatcherThread cycleWatcherThread;

	public CycleWatcher(int cycleTime) {
		super();
		cycleWatcherThread = new CycleWatcherThread(cycleTime);
		cycleWatcherThread.start();
	}

	@Override
	public void close() throws IOException {

		try {
			super.close();
		} finally {
			cycleWatcherThread.close();
		}
	}

	/**
	 * 
	 */
	@Override
	protected void checkClosed() {

		super.checkClosed();
		if (cycleWatcherThread.isClosed())
			throw new IllegalStateException("CycleWatcher has closed");
	}

	/**
	 * 
	 * @author lz31
	 *
	 * @param <T>
	 */
	class CycleWatcherThread extends Thread implements Closeable {

		private final AtomicBoolean closed = new AtomicBoolean(false);
		private final int cycleTime;

		public CycleWatcherThread(int cycleTime) {
			this.cycleTime = cycleTime;
		}

		@Override
		public void run() {

			while (!closed.get()) {

				if (!container.isEmpty()) {
					readLock.lock();
					try {
						for (Entry<T, AbstractConfigListener<T>> entry : container.entrySet()) {

							try {
								T config = entry.getKey();
								AbstractConfigListener<T> listener = entry.getValue();
								config.newInstanceAndNotify(listener);
							} catch (Exception ignore) {
							}
						}
					} finally {
						readLock.unlock();
					}
				}

				try {
					Thread.sleep(cycleTime);
				} catch (InterruptedException e) {
					return;// interrupted by close method
				}
			}
		}

		public boolean isClosed() {
			return closed.get();
		}

		@Override
		public void close() throws IOException {
			closed.set(true);
			this.interrupt();
		}
	}
}
