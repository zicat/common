package com.newegg.ec.ncommon.config.watcher.zookeeper;

import java.io.Closeable;
import java.io.IOException;

import com.newegg.ec.ncommon.config.ZookeeperConfig;
import com.newegg.ec.ncommon.config.listener.AbstractConfigListener;
import com.newegg.ec.ncommon.utils.ds.map.CommonEntry;

/**
 * 
 * @author lz31
 *
 */
public class ZookeeperConfigEntry extends CommonEntry<ZookeeperConfig<?>, AbstractConfigListener<ZookeeperConfig<?>>> implements Closeable {
	
	private Closeable nodeCache;
	
	public ZookeeperConfigEntry(ZookeeperConfig<?> k, AbstractConfigListener<ZookeeperConfig<?>> v, Closeable nodeCache) {
		
		super(k, v);
		this.nodeCache = nodeCache;
	}
	
	public ZookeeperConfig<?> getZookeeperConfig() {
		return getKey();
	}
	
	public AbstractConfigListener<ZookeeperConfig<?>> getListener() {
		return getValue();
	}

	@Override
	public void close() throws IOException {
		
		if(nodeCache != null)
			nodeCache.close();
	}
}
