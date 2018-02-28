package org.zicat.common.config.dao.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.utils.PathUtils;


import java.io.Closeable;

/**
 * 
 * @author lz31
 *
 */
public class CuratorZookeeperClient implements ZookeeperClient {

	private volatile CuratorFramework client;
	private static final CuratorZookeeperFactory DEFAULT_FACTORY = new CuratorZookeeperFactory();
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> DEFAULT_FACTORY.shutDownAll()));
	}
	
	public CuratorZookeeperClient(String hostPort, int sessionTimeout, CuratorZookeeperFactory factory) {
		this(hostPort, sessionTimeout / 2, sessionTimeout, factory == null?DEFAULT_FACTORY: factory);
	}
	
	public CuratorZookeeperClient(String hostPort, int sessionTimeout) {
		this(hostPort, sessionTimeout, DEFAULT_FACTORY);
	}
	
	/**
	 *
	 * @param hostPort
	 * @param connectionTimeout
	 * @param sessionTimeout
	 */
	public CuratorZookeeperClient(String hostPort, int connectionTimeout, int sessionTimeout, CuratorZookeeperFactory factory) {
		client = factory.getClient(hostPort, connectionTimeout, sessionTimeout);
	}
	
	@Override
	public byte[] getData(String path) throws Exception {
		
		PathUtils.validatePath(path);
		return client.getData().forPath(path);
	}
	
	@Override
	public boolean checkExists(String path) throws Exception {
		
		PathUtils.validatePath(path);
		return client.checkExists().forPath(path) != null;
	}
	
	@Override
	public void setData(String path, byte[] content) throws Exception {
		
		PathUtils.validatePath(path);
		client.setData().forPath(path, content);
	}

	@Override
	public boolean createNode(String path, byte[] value) throws Exception {
		PathUtils.validatePath(path);
		return client.create().creatingParentsIfNeeded().forPath(path, value) != null;
	}

	@Override
	public Closeable addNodeChangedWatcher(final String path, final NodeChangedHandler nodeChangedHandler) throws Exception {
		
		final NodeCache nodeCache = new NodeCache(client, path);
		nodeCache.getListenable().addListener(() -> nodeChangedHandler.process());
		nodeCache.start(true);
		return nodeCache;
	}

	@Override
	public long getModeMTime(String path) throws Exception {
		PathUtils.validatePath(path);
		return client.checkExists().forPath(path).getMtime();
	}
}

