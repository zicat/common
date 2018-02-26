package com.newegg.ec.ncommon.utils.pipeline;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JobContexts {
	
	private Map<String, JobContext<?>> jobContextMapping = new ConcurrentHashMap<>();
	
	public JobContexts() {
		
	}
	
	void addJobContext(JobContext<?> jobContext) {
		jobContextMapping.put(jobContext.getJobID(), jobContext);
	}
	
	public JobContext<?> findJobContext(Job<?> job) {
		return findJobContext(job.jobId());
	}
	
	public JobContext<?> findJobContext(String jobId) {
		
		return jobContextMapping.get(jobId);
	}
}
