package com.newegg.ec.ncommon.utils.test.pipeline;

import com.newegg.ec.ncommon.utils.pipeline.Job;
import com.newegg.ec.ncommon.utils.pipeline.JobContexts;

public class Job1 implements Job<String> {
	
	public Job1() {
	}
	
	@Override
	public String jobId() {
		return "1";
	}

	@Override
	public String execute(JobContexts jobContexts) throws Exception {
		System.out.println("execute job 1 start");
		throw new RuntimeException("job 1 exception");
	}
}
