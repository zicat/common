package com.newegg.ec.ncommon.utils.test.pipeline;

import com.newegg.ec.ncommon.utils.pipeline.Job;
import com.newegg.ec.ncommon.utils.pipeline.JobContexts;

public class Job101 implements Job<String> {
	
	public Job101() {
	}
	
	@Override
	public String jobId() {
		return "101";
	}

	@Override
	public String execute(JobContexts jobContexts) throws Exception {
		System.out.println(jobId() + " start");
		System.out.println(jobId() + " end");
		return "101";
	}
}
