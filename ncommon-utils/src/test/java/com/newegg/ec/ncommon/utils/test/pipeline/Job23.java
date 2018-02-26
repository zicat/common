package com.newegg.ec.ncommon.utils.test.pipeline;

import com.newegg.ec.ncommon.utils.pipeline.JobContexts;

public class Job23 extends Job21{

	public Job23() {
		super();
	}

	@Override
	public String jobId() {
		return "23";
	}
	
	@Override
	public Integer execute(JobContexts jobContexts) throws Exception {
		System.out.println("execute job " + jobId() +" start");
		return Integer.valueOf(jobId());
	}
}
