package org.zicat.common.utils.healthcheck;

import java.util.List;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public interface NodeSelectHandler<T, P> {
	
	public T select(final List<T> healthNodes, P payLoad);
	
	public void nodeChanged(final List<T> allNodes);
}
