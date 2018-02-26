package com.newegg.ec.ncommon.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.newegg.ec.ncommon.config.dao.zookeeper.CuratorZookeeperClient;
import com.newegg.ec.ncommon.config.dao.zookeeper.ZookeeperClient;
import com.newegg.ec.ncommon.config.schema.InputStreamSchema;
import com.newegg.ec.ncommon.utils.io.IOUtils;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public class ZookeeperConfig<T> extends AbstractConfig<String, T> {
	
	protected final String zookeeperHost;
	protected final String zookeeperPath;
	protected final InputStreamSchema<T> schema;
	protected final int timeout;
	
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
	
}
