package org.zicat.common.config.test;

import java.io.IOException;

import org.apache.curator.test.TestingServer;

/**
 * 
 * @author lz31
 * @scrope testing
 *
 */
public class ZookeeperServer {
	
	private TestingServer testingServer;

	public void initZK() throws Exception {
		
		testingServer = new TestingServer();
		testingServer.start();
	}
	
	public String getHostPort() {
		return testingServer.getConnectString();
	}
	
	public void closeZK() throws IOException {
		testingServer.close();
	}
}
