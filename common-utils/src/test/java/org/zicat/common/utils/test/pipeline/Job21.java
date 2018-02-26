package org.zicat.common.utils.test.pipeline;

import org.zicat.common.utils.pipeline.Job;
import org.zicat.common.utils.pipeline.JobContext;
import org.zicat.common.utils.pipeline.JobContexts;

public class Job21 implements Job<Integer> {
	
	public Job21() {
	}
	
	@Override
	public String jobId() {
		return "21";
	}

	@Override
	public Integer execute(JobContexts jobContexts) throws Exception {
		System.out.println("execute job " + jobId() +" start");
		Thread.sleep(100);
		JobContext<?> job1Context = jobContexts.findJobContext("1");
		if(job1Context.getException() != null) {
			System.out.println("job 1 exception " + job1Context.getException());
			throw job1Context.getException();
		}
		System.out.println("job" + jobId() + "get job 1 value " + job1Context.getResult());
		return Integer.valueOf(jobId());
	}
}
