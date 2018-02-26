package com.newegg.ec.ncommon.utils.pipeline;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Pipeline is a special Job, the jobs added to pipeline will be executed serially
 * @author lz31
 *
 */
public final class Pipeline implements Job<Pipeline> {
	
	final Queue<Job<?>> jobQueue;
	final String pipelineId;
	
	public Pipeline(String pipelineId) {
		
		this.pipelineId = pipelineId;
		this.jobQueue = new ConcurrentLinkedQueue<>();
	}
	
	@Override
	public String jobId() {
		return pipelineId;
	}

	public Pipeline addJob(Job<?> job) {
		jobQueue.add(job);
		return this;
	}
	
	@Override
	public Pipeline execute(JobContexts jobContexts) throws Exception {
		
		while(!jobQueue.isEmpty()) {
			Job<?> job = jobQueue.poll();
			Object value = null;
			Exception e = null;
			try {
				value = job.execute(jobContexts);
			} catch(Exception e1) {
				e = e1;
			}
			
			if(!(job instanceof Pipeline || job instanceof Layer)) {
				JobContext<?> context = new JobContext<>(job.jobId(), new FutureImpl<>(value, e));
				context.setException(e);
				jobContexts.addJobContext(context);
			}
		}
		return this;
	}
}
