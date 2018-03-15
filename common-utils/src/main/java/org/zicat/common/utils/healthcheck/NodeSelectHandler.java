package org.zicat.common.utils.healthcheck;

import java.util.List;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public interface NodeSelectHandler<T, P> {

	/**
	 * 
	 * @param healthNodes
	 * @param payLoad
	 * @return
	 */
	public T select(final List<T> healthNodes, P payLoad);

	/**
	 * 
	 * @param allNodes
	 */
	public void nodeChanged(final List<T> allNodes);
}
