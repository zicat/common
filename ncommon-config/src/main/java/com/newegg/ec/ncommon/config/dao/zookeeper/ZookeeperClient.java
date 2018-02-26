package com.newegg.ec.ncommon.config.dao.zookeeper;

import java.io.Closeable;

/**
 * 
 * @author lz31
 *
 */
public interface ZookeeperClient {

    /**
     * check the node is existed whether or not
     *
     * @param path
     * @return
     * @throws Exception
     */
    boolean checkExists(String path) throws Exception;

    /**
     * get data from the node according to the path
     * @param path
     * @return
     * @throws Exception
     */
    byte[] getData(String path) throws Exception;
    
    /**
     * set data to the node according to path
     *
     * @param path
     * @param content
     * @return
     * @throws Exception
     */
    void setData(String path, byte[] content) throws Exception;
    
    /**
     * create the node according to the path
     *
     * @param path
     * @return
     * @throws Exception
     */
    boolean createNode(String path, byte[] value) throws Exception;
    
    /**
     * 
     * @param path
     * @param watcherHandler
     * @throws Exception
     */
    Closeable addNodeChangedWatcher(String path, NodeChangedHandler nodeChangedHandler) throws Exception;
    /**
     *
     */
    interface NodeChangedHandler {
    	
        void process();
    }
}
