package org.zicat.common.config.watcher.local;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.zicat.common.config.LocalConfig;
import org.zicat.common.config.listener.AbstractConfigListener;
import org.zicat.common.config.watcher.CycleWatcher;
import org.zicat.common.config.watcher.Watcher;
import org.zicat.common.utils.io.IOUtils;

/**
 * implements Watcher by jdk nio WatchService
 * @ThreadSafe
 * @author lz31
 *
 */
public class LocalWatcher implements Watcher<LocalConfig<?>> {
	
	private Map<Path, LocalWatcherThread> pathContainer = new ConcurrentHashMap<>();
	private long delayTime = -1;
	
	public static boolean isOSX = false;
	private Watcher<LocalConfig<?>> osxWatcherWrap = null;
	
	static {
		isOSX = System.getProperty("os.name").equals("Mac OS X");
	}
	
	public LocalWatcher() {
		this(-1);
	}
	
	public LocalWatcher(long delayTime) {
		this.delayTime = delayTime;
		if(isOSX) {
			osxWatcherWrap = new CycleWatcher<>(1000);
		}
	}
	
	@Override
	public void register(LocalConfig<?> config) throws Exception {
		register(config, null);
	}
	
	@Override
	public void register(LocalConfig<?> localConfig, AbstractConfigListener<LocalConfig<?>> listener) throws Exception {
		
		if(localConfig == null)
			throw new NullPointerException("local config is null");
		
		if(isOSX) {
			osxWatcherWrap.register(localConfig, listener);
			return;
		}
		Path dir = localConfig.getDirPath();
		synchronized (this) {
			LocalWatcherThread localWatcherThread = pathContainer.get(dir);
			if(localWatcherThread == null) {
				localWatcherThread = new LocalWatcherThread(dir, delayTime);
				localWatcherThread.addLocalConfig(localConfig, listener);
				localWatcherThread.start();
				pathContainer.put(dir, localWatcherThread);
			} else if(!localWatcherThread.isClose() && !localWatcherThread.isAlive()) {
				Map<String, LocalConfigListenerEntry> oldLocalConfigContainer = localWatcherThread.localConfigContainer();
				LocalWatcherThread newLocalWatcherThread = new LocalWatcherThread(dir, delayTime);
				newLocalWatcherThread.addLocalConfig(localConfig, listener);
				for(Entry<String, LocalConfigListenerEntry> entry: oldLocalConfigContainer.entrySet()) {
					newLocalWatcherThread.addLocalConfig(entry.getValue().getLocalConfig(), entry.getValue().getConfigListener());
				}
				newLocalWatcherThread.start();
				pathContainer.put(dir, newLocalWatcherThread);
				IOUtils.closeQuietly(localWatcherThread);
			} else {
				localWatcherThread.addLocalConfig(localConfig, listener);
			}
		}
	}

	@Override
	public void unregister(LocalConfig<?> config) throws Exception {
		
		if(config == null)
			throw new NullPointerException("config is null");
		
		if(isOSX) {
			osxWatcherWrap.unregister(config);
			return;
		}
			
		Path dir = config.getDirPath();
		synchronized (this) {
			LocalWatcherThread localWatcherThread = pathContainer.get(dir);
			if(localWatcherThread == null)
				return;
			localWatcherThread.removeLocalConfig(config);
		}
	}

	@Override
	public synchronized void close() throws IOException {
		
		if(isOSX) {
			osxWatcherWrap.close();
			return;
		}
		for(Entry<Path, LocalWatcherThread> pathThreadEntry: pathContainer.entrySet()) {
			IOUtils.closeQuietly(pathThreadEntry.getValue());
		}
		pathContainer.clear();
	}
}
