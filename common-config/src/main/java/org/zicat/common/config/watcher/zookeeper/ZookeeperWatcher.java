package org.zicat.common.config.watcher.zookeeper;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zicat.common.config.ZookeeperConfig;
import org.zicat.common.config.listener.AbstractConfigListener;
import org.zicat.common.config.listener.LoggerConfigListener;
import org.zicat.common.config.watcher.Watcher;
import org.zicat.common.utils.io.IOUtils;

/**
 * 
 * @ThreadSafe
 * @author lz31
 *
 */
public class ZookeeperWatcher implements Watcher<ZookeeperConfig<?>> {

	private Map<ZookeeperConfig<?>, ZookeeperConfigEntry> container = new ConcurrentHashMap<>();
	private static final Logger LOG = LoggerFactory.getLogger(ZookeeperWatcher.class);

	private static final AbstractConfigListener<ZookeeperConfig<?>> DEFAULT_LISTENER = new LoggerConfigListener<>();

	@Override
	public void register(final ZookeeperConfig<?> config, final AbstractConfigListener<ZookeeperConfig<?>> listener)
			throws Exception {

		if (config == null)
			throw new NullPointerException("zookeeper config is null");

		synchronized (this) {

			if (container.containsKey(config))
				return;

			config.newInstance();
			final AbstractConfigListener<ZookeeperConfig<?>> realListener = listener == null ? DEFAULT_LISTENER: listener;
			realListener.init(config);
			Closeable nodeCache = config.getZkClient().addNodeChangedWatcher(config.getZookeeperPath(), () -> {
				try {
					config.newInstanceAndNotify(realListener);
				} catch (Exception e) {
					LOG.error("zookeeper changed callback error", e);
				}
			});
			final ZookeeperConfigEntry zookeeperConfigEntry = new ZookeeperConfigEntry(config, realListener, nodeCache);
			container.put(config, zookeeperConfigEntry);
		}
	}

	@Override
	public void register(ZookeeperConfig<?> config) throws Exception {
		register(config, null);
	}

	@Override
	public void unregister(ZookeeperConfig<?> config) throws Exception {

		if (config == null)
			throw new NullPointerException("zookeeper config is null");

		final ZookeeperConfigEntry zookeeperConfigEntry = container.remove(config);
		if (zookeeperConfigEntry == null)
			return;

		zookeeperConfigEntry.close();
	}

	@Override
	public synchronized void close() throws IOException {

		for (Entry<ZookeeperConfig<?>, ZookeeperConfigEntry> entry : container.entrySet()) {
			IOUtils.closeQuietly(entry.getValue());
		}
		container.clear();
	}
}
