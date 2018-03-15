package org.zicat.common.config.dao.zookeeper;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import org.zicat.common.utils.io.IOUtils;

/**
 * 
 * @author lz31
 *
 */
public class CuratorZookeeperFactory {

	private final Map<String, CuratorFramework> zkManager = new ConcurrentHashMap<>();

	/**
	 * 
	 * @param hostPort
	 * @param connectionTimeout
	 * @param sessionTimeout
	 * @return
	 */
	protected CuratorFramework getClient(String hostPort, int connectionTimeout, int sessionTimeout) {

		CuratorFramework client = zkManager.get(hostPort);
		if (client != null)
			return client;

		synchronized (this) {

			client = zkManager.get(hostPort);
			if (client != null)
				return client;

			client = CuratorFrameworkFactory.builder().connectString(hostPort)
					.retryPolicy(new ExponentialBackoffRetry(1000, 3)).connectionTimeoutMs(connectionTimeout)
					.sessionTimeoutMs(sessionTimeout).build();
			client.start();
			zkManager.put(hostPort, client);
		}
		return client;
	}

	public synchronized void shutDown(String hostPort) {

		CuratorFramework client = zkManager.remove(hostPort);
		IOUtils.closeQuietly(client);
	}

	/**
	 *
	 */
	public synchronized void shutDownAll() {

		if (!zkManager.isEmpty()) {
			for (Entry<String, CuratorFramework> entry : zkManager.entrySet()) {
				IOUtils.closeQuietly(entry.getValue());
			}
			zkManager.clear();
		}
	}
}
