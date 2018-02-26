package org.zicat.common.utils.healthcheck;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public interface NodePing<T> {
	
	public boolean ping(T node) throws Throwable;
	
	public int scanIntervals();
}
