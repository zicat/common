package org.zicat.common.utils.ds.queue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author lz31
 * @ThreadSafe
 */
public class StringSerializableHandler implements SerializableHandler<String> {

	private Charset charset;

	public StringSerializableHandler(Charset charset) {

		if (charset == null) 
			throw new NullPointerException("charset is null");
		
		this.charset = charset;
	}

	public StringSerializableHandler() {

		this(StandardCharsets.UTF_8);
	}

	@Override
	public byte[] serialize(String e) throws IOException {
		return e.getBytes(charset);
	}

	@Override
	public String deserialize(byte[] bs) throws IOException {
		return new String(bs, charset);
	}
}