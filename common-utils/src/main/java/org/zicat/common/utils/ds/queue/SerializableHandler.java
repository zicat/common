package org.zicat.common.utils.ds.queue;

import java.io.IOException;

/**
 * @author lz31
 * @ThreadSafe
 * @param <E>
 */
public interface SerializableHandler<E> {

	/**
	 *
	 * @param e
	 * @return
	 */
	byte[] serialize(E e) throws IOException;

	/**
	 *
	 * @param bs
	 * @return
	 */
	E deserialize(byte[] bs) throws IOException;
}
