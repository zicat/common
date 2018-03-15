package org.zicat.common.utils.healthcheck;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public interface NodePing<T> {

	/**
	 * 
	 * @param node
	 * @return
	 * @throws Throwable
	 */
	public boolean ping(T node) throws Throwable;

	/**
	 * 
	 * @return
	 */
	public int scanIntervals();
}
