package org.zicat.common.http.client.factory;

import javax.ws.rs.client.Client;

/**
 * Created by lz31 on 2017/9/21.
 */
public interface ClientFactory {

	/**
	 *
	 * @return
	 */
	Client createClient();

	/**
	 *
	 */
	void destory(Client client);

}
