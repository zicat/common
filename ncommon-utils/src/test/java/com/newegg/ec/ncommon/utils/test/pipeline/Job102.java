package com.newegg.ec.ncommon.utils.test.pipeline;

import com.newegg.ec.ncommon.utils.pipeline.Job;
import com.newegg.ec.ncommon.utils.pipeline.JobContext;
import com.newegg.ec.ncommon.utils.pipeline.JobContexts;

public class Job102 implements Job<String> {
	
	public Job102() {
	}
	
	@Override
	public String jobId() {
		return "102";
	}

	@Override
	public String execute(JobContexts jobContexts) throws Exception {
		System.out.println(jobId() + " start");
		JobContext<?> job101Context = jobContexts.findJobContext("101");
		System.out.println(jobId() + " print job 101 result:" + job101Context.getResult());
		System.out.println(jobId() + " end");
		return "102 result";
	}

}