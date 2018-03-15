package org.zicat.common.http.client;

import javax.ws.rs.client.Client;

/**
 * Created by lz31 on 2017/9/21.
 */
public interface RequestHandler<T> {

	/**
	 * request and return response
	 * 
	 * @param client
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	T callback(Client client) throws Exception;
}
