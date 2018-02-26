package com.newegg.ec.ncommon.utils.ds.queue;

import java.io.IOException;

/**
 * 
 * @author lz31
 * @ThreadSafe
 */
public class ByteSerializableHandler implements SerializableHandler<byte[]> {

	@Override
	public byte[] serialize(byte[] e) throws IOException {
		return e;
	}

	@Override
	public byte[] deserialize(byte[] bs) throws IOException {
		return bs;
	}
}
