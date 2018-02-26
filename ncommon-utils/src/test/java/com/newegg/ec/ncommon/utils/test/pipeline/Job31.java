package com.newegg.ec.ncommon.utils.test.pipeline;

import com.newegg.ec.ncommon.utils.pipeline.Job;
import com.newegg.ec.ncommon.utils.pipeline.JobContext;
import com.newegg.ec.ncommon.utils.pipeline.JobContexts;

public class Job31 implements Job<String>{

	@Override
	public String jobId() {
		return "31";
	}

	@Override
	public String execute(JobContexts jobContexts) throws Exception {
		System.out.println("job 31 start");
		JobContext<?> job21 = jobContexts.findJobContext("21");
		JobContext<?> job22 = jobContexts.findJobContext("22");
		JobContext<?> job23 = jobContexts.findJobContext("23");
		if(job21.getException() == null)
			System.out.println("job 31 print job 21 value " + job21.getResult());
		
		if(job22.getException() == null)
			System.out.println("job 31 print job 22 value " + job22.getResult());
		
		if(job23.getException() == null)
			System.out.println("job 31 print job 23 value " + job23.getResult());
		
		System.out.println("job 31 end");
		return "job31";
	}
}
