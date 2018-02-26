package org.zicat.common.utils.pipeline;

public interface Job<T> {
	
	/**
	 * Get Job ID
	 * @return
	 */
	public String jobId();
	
	/**
	 * Execute job
	 * @param jobContexts
	 * @return
	 * @throws Throwable
	 */
	public T execute(JobContexts jobContexts) throws Exception;
	
}
