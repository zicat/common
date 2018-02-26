package com.newegg.ec.ncommon.http.client.factory;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;

import com.newegg.ec.ncommon.http.client.RequestHandler;

/**
 * 
 * @author lz31
 *
 */
public abstract class AbstractClientFactory implements ClientFactory, Closeable {
	
	private Client client;
    private ExecutorService executorService;
    
    protected final int aSynHttpThreadCount;
    
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final ReadLock readLock = lock.readLock();
	private final WriteLock writeLock = lock.writeLock();
    
    public AbstractClientFactory(int aSynHttpThreadCount) {
    	
        this.aSynHttpThreadCount = aSynHttpThreadCount;
        client = createClient();
    }
    
    @Override
    public Client createClient() {
    	
        ExecutorService executorService = aSynHttpThreadCount > 0? Executors.newFixedThreadPool(aSynHttpThreadCount): Executors.newSingleThreadExecutor();
        ClientBuilder builder = ClientBuilder.newBuilder().executorService(executorService);
        Client client = builder.withConfig(buildConfig()).build();
        this.executorService = executorService;
        return client;
    }
    
    /**
     * class can implements JerseyClientFactory.class and set property and property value
     * @return
     */
    protected abstract Configuration buildConfig();
    
    @Override
    public void close() {
    	
    	final WriteLock writeLock = this.writeLock;
		try {
			writeLock.lock();
			destory(client);
		} finally {
			writeLock.unlock();
		}
    }
    
    @Override
    public void destory(Client client) {
    	
    	if(client == null)
    		return;
			
		if(client != this.client) {
			client.close();
			return;
		}
		
		shutdownExecutorService();
		client.close();
    }
    
    /**
     * 
     */
    private void shutdownExecutorService() {
    	
    	if(executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }
    
    /**
	 * 
	 * @param requestHandler
	 * @return
	 * @throws Exception
	 */
	public <T> T request(RequestHandler<T> requestHandler) throws Exception {
		
		final ReadLock readLock = this.readLock;
		try {
			readLock.lock();
			return requestHandler.callback(client);
		} finally {
			readLock.unlock();
		}
	}
    
    /**
     * 
     */
	public void reload() {
		
		final WriteLock writeLock = this.writeLock;
		try {
			writeLock.lock();
			destory(client);
			client = createClient();
		} finally {
			writeLock.unlock();
		}
	}
}
