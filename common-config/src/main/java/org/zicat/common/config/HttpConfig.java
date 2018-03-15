package org.zicat.common.config;

import java.io.InputStream;

import org.zicat.common.config.schema.InputStreamSchema;
import org.zicat.common.http.client.RestfullClient;
import org.zicat.common.http.client.factory.AbstractClientFactory;
import org.zicat.common.http.client.factory.jersey.GrizzlyJerseyClientFactory;
import org.zicat.common.utils.io.IOUtils;

/**
 * 
 * @author zicat
 *
 * @param <S>
 * @param <T>
 */
public abstract class HttpConfig<S, T> extends AbstractConfig<S, T> {

	private static final AbstractClientFactory DEFAULT_CLIENT_FACTORY = new GrizzlyJerseyClientFactory(10, 3000, 3000);

	protected AbstractClientFactory clientFactory;
	protected InputStreamSchema<T> schema;

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> DEFAULT_CLIENT_FACTORY.close()));
	}

	public HttpConfig(AbstractConfig<?, T> parentConfig, S source, AbstractClientFactory clientFactory, InputStreamSchema<T> schema) {

		super(parentConfig, source);
		if (clientFactory == null)
			throw new NullPointerException("client factory is null");

		if (schema == null)
			throw new NullPointerException("schema is null");

		this.clientFactory = clientFactory;
		this.schema = schema;
	}

	public HttpConfig(AbstractConfig<?, T> parentConfig, S source, InputStreamSchema<T> schema) {
		this(parentConfig, source, DEFAULT_CLIENT_FACTORY, schema);
	}

	@Override
	protected T newInstance(T parentInstance) throws Exception {

		InputStream stream = null;
		try {
			RestfullClient restfullClient = new RestfullClient(clientFactory);
			stream = newInstanceInputStream(restfullClient);
			return createInstanceBySchema(schema, stream);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	/**
	 * 
	 * @param restfullClient
	 * @return
	 */
	protected abstract InputStream newInstanceInputStream(RestfullClient restfullClient);
}
