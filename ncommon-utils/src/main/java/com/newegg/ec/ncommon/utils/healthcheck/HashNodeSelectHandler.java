package com.newegg.ec.ncommon.utils.healthcheck;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public class HashNodeSelectHandler<T> implements NodeSelectHandler<T, String> {

	protected Long hash(String key) {

		ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
		int seed = 0x1234ABCD;

		ByteOrder byteOrder = buf.order();
		buf.order(ByteOrder.LITTLE_ENDIAN);

		long m = 0xc6a4a7935bd1e995L;
		int r = 47;

		long h = seed ^ (buf.remaining() * m);

		long k;
		while (buf.remaining() >= 8) {
			k = buf.getLong();

			k *= m;
			k ^= k >>> r;
			k *= m;

			h ^= k;
			h *= m;
		}

		if (buf.remaining() > 0) {
			ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
			finish.put(buf).rewind();
			h ^= finish.getLong();
			h *= m;
		}

		h ^= h >>> r;
		h *= m;
		h ^= h >>> r;

		buf.order(byteOrder);
		return h;
	}

	@Override
	public T select(final List<T> healthNodes, String payLoad) {

		if (healthNodes == null || healthNodes.isEmpty())
			return null;

		int size = healthNodes.size();
		Long hashCode = Math.abs(hash(payLoad));
		return healthNodes.get((int) (hashCode % size));
	}

	@Override
	public void nodeChanged(List<T> allNodes) {
		
	}
}
