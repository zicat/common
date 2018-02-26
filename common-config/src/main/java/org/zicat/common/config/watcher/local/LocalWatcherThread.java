package org.zicat.common.config.watcher.local;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zicat.common.config.LocalConfig;
import org.zicat.common.config.listener.AbstractConfigListener;
import org.zicat.common.config.listener.LoggerConfigListener;
import org.zicat.common.utils.io.IOUtils;

/**
 * 
 * @author lz31
 *
 */
public class LocalWatcherThread extends Thread implements Closeable {
	
	private static final Logger LOG = LoggerFactory.getLogger(LocalWatcherThread.class);
	private static final AbstractConfigListener<LocalConfig<?>> DEFAULT_LISTENER = new LoggerConfigListener<>();
	
	private volatile WatchService watchService;
	private volatile WatchKey key;
	private final Map<String, LocalConfigListenerEntry> localConfigContainer = new ConcurrentHashMap<>(); 
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final Path dirPath;
	private final long delayTime;
	
	public LocalWatcherThread(Path dirPath) throws IOException {
		this(dirPath, -1);
	}
	
	public LocalWatcherThread(Path dirPath, long delayTime) throws IOException {
		
		this.dirPath = dirPath;
		this.delayTime = delayTime;
		createWatchService();
	}
	
	/**
	 * 
	 * @param localConfig
	 * @param configListener
	 * @throws Exception 
	 */
	public void addLocalConfig(LocalConfig<?> localConfig, AbstractConfigListener<LocalConfig<?>> configListener) throws Exception {
		
		if(localConfig == null)
			return;
		
		if(configListener == null)
			configListener = DEFAULT_LISTENER;
		
		String name = localConfig.getName();
		LocalConfigListenerEntry entry = new LocalConfigListenerEntry(localConfig, configListener, delayTime);
		
		synchronized (this) {
			if(!localConfigContainer.containsKey(name)) {
				localConfig.newInstance();
				localConfigContainer.put(name, entry);
			}
		}
	}
	
	/**
	 * 
	 * @param localConfig
	 */
	public void removeLocalConfig(LocalConfig<?> localConfig) {
		
		if(localConfig == null)
			return;
		
		localConfigContainer.remove(localConfig.getName());
	}
	
	@Override
	public void run() {
		
		while(!closed.get()) {
			
			WatchKey key;
			try {
				key = watchService.poll(3, TimeUnit.SECONDS);
				if(key == null)
					continue;
				
				for (WatchEvent<?> event : key.pollEvents()) {
					
					String name = event.context().toString();
					LocalConfigListenerEntry localConfigEntry = localConfigContainer.get(name);
					if(localConfigEntry == null)
						continue;
					
					try {
						localConfigEntry.listenerCallback();
					} catch(Exception ignore) {
						LOG.error("local watch listener call back error", ignore);
					}
				}
				if(!key.reset()) {
					try {
						key.cancel();
						IOUtils.closeQuietly(watchService); //close old watch service
						createWatchService();
					} catch(Exception e) {
						LOG.error("Watch Key reset fail and rebuild watch service fail, path:" + dirPath, e);
					}
				}
			} catch (InterruptedException e) {
				return;// interrupted by close method
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, LocalConfigListenerEntry> localConfigContainer() {
		return localConfigContainer;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isClose() {
		return closed.get();
	}
	
	@Override
	public void close() throws IOException {
		
		closed.set(true);
		try {
			key.cancel();
		} finally {
			interrupt();
			IOUtils.closeQuietly(watchService);
		}
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void createWatchService() throws IOException {
		
		this.watchService = FileSystems.getDefault().newWatchService();
		this.key = dirPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
	}
}
