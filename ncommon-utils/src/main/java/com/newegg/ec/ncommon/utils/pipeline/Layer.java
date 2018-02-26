package com.newegg.ec.ncommon.utils.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.newegg.ec.ncommon.utils.ds.map.CommonEntry;

/**
 * Layer is a special Job, the jobs added to Layer will be executed parallel
 * @author lz31
 *
 */
public final class Layer implements Job<Layer> {
	
	private Layer nextLayer;
	private List<Job<?>> jobs = new ArrayList<>();
	private Map<Job<?>, Entry<TimeUnit, Long>> jobsTimeOut = new HashMap<Job<?>, Entry<TimeUnit, Long>>();
	
	private final ExecutorService executorService;
	private final String layerId;
	
	public Layer(String layerId, ExecutorService executorService) {
		
		this.layerId = layerId;
		this.executorService = executorService;
	}
	
	public void setNextLayer(Layer nextLayer) {
		this.nextLayer = nextLayer;
	}
	
	public Layer addJob(Job<?> job, TimeUnit unit, long timeout) {
		this.jobs.add(job);
		this.jobsTimeOut.put(job, new CommonEntry<TimeUnit, Long>(unit, timeout));
		return this;
	}
	
	public Layer addJob(Job<?> job) {
		this.jobs.add(job);
		return this;
	}
	
	public Layer execute(final JobContexts jobContexts) {
		
		Map<Job<?>, JobContext<?>> mapping = new HashMap<>();
		
		for(final Job<?> job: jobs) {
			
			JobContext<?> jobContext = new JobContext<>(job.jobId(), executorService.submit(new Callable<Object>(){
				
				@Override
				public Object call() throws Exception {
					
					return job.execute(jobContexts);
				}
			}));
			if(!(job instanceof Pipeline || job instanceof Layer)) {
				jobContexts.addJobContext(jobContext);
			}
			mapping.put(job, jobContext);
		}
		
		for(Entry<Job<?>, JobContext<?>> jobEntry: mapping.entrySet()) {
			
			JobContext<?> jobContext = jobEntry.getValue();
			try {
				Entry<TimeUnit, Long> entry = jobsTimeOut.get(jobEntry.getKey());
				if(entry != null) {
					jobContext.getResult(entry.getKey(), entry.getValue());
				} else {
					jobContext.getResult();
				}
			} catch (Exception exception) {
				jobContext.setException(exception);
			}
		}
		
		if(nextLayer != null) {
			nextLayer.execute(jobContexts);
		}
		return this;
	}

	@Override
	public String jobId() {
		return layerId;
	}
}
