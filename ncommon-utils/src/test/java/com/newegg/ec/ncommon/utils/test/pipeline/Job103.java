package com.newegg.ec.ncommon.utils.test.pipeline;

import com.newegg.ec.ncommon.utils.pipeline.Job;
import com.newegg.ec.ncommon.utils.pipeline.JobContext;
import com.newegg.ec.ncommon.utils.pipeline.JobContexts;

public class Job103 implements Job<String> {
	
	public Job103() {
	}
	
	@Override
	public String jobId() {
		return "103";
	}

	@Override
	public String execute(JobContexts jobContexts) throws Exception {
		System.out.println(jobId() + " start");
		JobContext<?> job101Context = jobContexts.findJobContext("101");
		System.out.println(jobId() + " print job 101 result:" + job101Context.getResult());
		Thread.sleep(1000);
		System.out.println(jobId() + " end");
		throw new RuntimeException("exec job 103 error");
	}
}
