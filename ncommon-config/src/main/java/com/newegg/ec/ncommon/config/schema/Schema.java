package com.newegg.ec.ncommon.config.schema;

/**
 * 
 * @author lz31
 * @ThreadSafe
 * @param <S>
 * @param <T>
 */
public interface Schema<S, T> {
	
	/**
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	T unmarshal(S source) throws Exception;
	
	/**
	 * 
	 * @param parentTarget
	 * @param source
	 * @return
	 * @throws Exception
	 */
	T unmarshal(T parentTarget, S source) throws Exception;
}
