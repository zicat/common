package com.newegg.ec.ncommon.utils.pipeline;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class JobContext<T> {
	
	private final String jobID;
	private final Future<T> result;
	private Exception exception;
	
	public JobContext(String jobID, Future<T> f) {
		
		this.jobID = jobID;
		this.result = f;
	}
	

	public String getJobID() {
		return jobID;
	}
	
	void setException(Exception exception) {
		this.exception = exception;
	}
	
	/**
	 * Blocking until job finished
	 * @return
	 * @throws Throwable 
	 */
	public T getResult() throws Exception {
		
		if(exception != null)
			throw exception;
		
		return result.get();
	}
	
	T getResult(TimeUnit unit, long timeout) throws Exception {
		if(exception != null)
			throw exception;
		return result.get(timeout, unit);
	}
	
	/**
	 * Get exception
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Exception getException() throws InterruptedException, ExecutionException {
		return exception;
	}
}
