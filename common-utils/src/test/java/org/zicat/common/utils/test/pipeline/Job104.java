package org.zicat.common.utils.test.pipeline;

import org.zicat.common.utils.pipeline.Job;
import org.zicat.common.utils.pipeline.JobContext;
import org.zicat.common.utils.pipeline.JobContexts;

public class Job104 implements Job<String>{
	
	public Job104() {
	}
	
	
	@Override
	public String jobId() {
		return "104";
	}

	@Override
	public String execute(JobContexts jobContexts) throws Exception {
		System.out.println(jobId() + " start");
		JobContext<?> job101Context = jobContexts.findJobContext("103");
		if(job101Context.getException() == null) {
			System.out.println(jobId() + " print job 103 result:" + job101Context.getResult());
		} else {
			System.out.println(jobId() + " print job 103 Exception:" + job101Context.getException());
		}
		System.out.println(jobId() + " end");
		return "104 result";
	}
}
