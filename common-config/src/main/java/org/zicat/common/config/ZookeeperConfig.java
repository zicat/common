package org.zicat.common.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zicat.common.config.dao.zookeeper.CuratorZookeeperClient;
import org.zicat.common.config.dao.zookeeper.ZookeeperClient;
import org.zicat.common.config.schema.InputStreamSchema;
import org.zicat.common.utils.io.IOUtils;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public class ZookeeperConfig<T> extends AbstractConfig<String, T> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ZookeeperConfig.class);
	
	protected final String zookeeperHost;
	protected final String zookeeperPath;
	protected final InputStreamSchema<T> schema;
	protected final int timeout;
	protected volatile long lasteditTime = -1;
	
	public ZookeeperConfig(String zookeeperHost, String zookeeperPath, int timeout, InputStreamSchema<T> schema, AbstractConfig<String, T> parentConfig) {
		
		super(parentConfig, zookeeperHost + zookeeperPath);
		
		if(schema == null)
			throw new NullPointerException("schema is null");

		if(zookeeperHost == null)
			throw new NullPointerException("zookeeper host is null");

		if(zookeeperPath == null)
			throw new NullPointerException("zookeeper path is null");

		this.zookeeperHost = zookeeperHost;
		this.zookeeperPath = zookeeperPath;
		this.schema = schema;
		this.timeout = timeout;
	}

	public ZookeeperConfig(String zookeeperHost, String zookeeperPath, int timeout, InputStreamSchema<T> schema) {
		this(zookeeperHost, zookeeperPath, timeout, schema, null);
	}
	
	@Override
	protected T newInstance(T parentInstance) throws Exception {
		
		InputStream stream = null;
		try {
			ZookeeperClient client = getZkClient();
			if(!client.checkExists(zookeeperPath))
				return null;
			
			byte[] data = client.getData(zookeeperPath);
			stream = new ByteArrayInputStream(data);
			return createInstanceBySchema(schema, stream);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ZookeeperClient getZkClient() {
		return new CuratorZookeeperClient(zookeeperHost, timeout);
	}

	/**
	 * 
	 * @return
	 */
	public final String getZookeeperHost() {
		return zookeeperHost;
	}
	
	/**
	 * 
	 * @return
	 */
	public final String getZookeeperPath() {
		return zookeeperPath;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTimeout() {
		return timeout;
	}
	
	@Override
	public boolean isModify() {
		
		try {
			long currentModify = getZkClient().getModeMTime(zookeeperPath);
			if(currentModify != lasteditTime) {
				lasteditTime = currentModify;
				return true;
			}
		} catch (Exception e) {
			LOG.error("get mtime for path " + zookeeperPath + " error", e);
		}
		return false;
	}
}
