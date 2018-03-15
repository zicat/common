package org.zicat.common.utils.dp;

import java.util.List;

/**
 * 
 * @author lz31
 * @ThreadSafe
 * @param <E>
 */
public interface ConsumerHandler<E> {

	/**
	 * Consumer Elements Producer Elements will partition if threadCount > 1
	 * Override Consumer:subList(List<E> list, int size) to change default partition
	 * logic if need
	 * 
	 * @param elements
	 * @throws Exception
	 */
	void consume(List<E> elements) throws Exception;

	/**
	 * Deal Exception If dealException not throw Exception, will callback
	 * Consumer:consumerSuccess() If dealException throw Exception, will callback
	 * Consumer:consumerFailure()
	 * 
	 * @param elements
	 * @param e
	 */
	void dealException(List<E> elements, Exception e);
}
