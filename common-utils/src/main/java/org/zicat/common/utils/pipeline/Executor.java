package org.zicat.common.utils.pipeline;

public class Executor {
	
	private Job<?> header;
	private JobContexts jobContexts = new JobContexts();
	
	public Executor(Job<?> header) {
		
		this.header = header;
	}
	
	public JobContexts execute() throws Exception {
		header.execute(jobContexts);
		return jobContexts;
	}
}
