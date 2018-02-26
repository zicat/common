package com.newegg.ec.ncommon.utils.test.pipeline;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.newegg.ec.ncommon.utils.pipeline.Executor;
import com.newegg.ec.ncommon.utils.pipeline.JobContext;
import com.newegg.ec.ncommon.utils.pipeline.JobContexts;
import com.newegg.ec.ncommon.utils.pipeline.Layer;
import com.newegg.ec.ncommon.utils.pipeline.Pipeline;

public class TestJbExector {
	
	@Test
	public void test1() throws Throwable {
		
		ExecutorService exe = Executors.newFixedThreadPool(20);
		try {
			Job31 job31 = new Job31();
			Job21 job21 = new Job21();
			Job22 job22 = new Job22();
			Job23 job23 = new Job23();
			Job1 job1 = new Job1();
			Layer layer1 = new Layer("layer1", exe);
			layer1.addJob(job1);
			Layer layer2 = new Layer("layer2", exe);
			layer2.addJob(job21, TimeUnit.MILLISECONDS, 200).addJob(job22, TimeUnit.MILLISECONDS, 200).addJob(job23);
			Layer layer3 = new Layer("layer3", exe);
			layer3.addJob(job31);
			layer1.setNextLayer(layer2);
			layer2.setNextLayer(layer3);
			
			Executor executor = new Executor(layer1);
			JobContexts jobContexts = executor.execute();
			
			JobContext<?> context1 = jobContexts.findJobContext("1");
			Assert.assertTrue(context1.getException() != null);
			
			JobContext<?> context21 = jobContexts.findJobContext("21");
			Assert.assertTrue(context21.getException() != null);
			
			JobContext<?> context23 = jobContexts.findJobContext("23");
			Assert.assertTrue(String.valueOf(context23.getResult()).equals("23"));
			JobContext<?> context = jobContexts.findJobContext("31");
			Assert.assertTrue(context.getResult().equals("job31"));
		} finally {
			exe.shutdown();
		}
	}
	
	@Test
	public void test2() throws Throwable {
		
		ExecutorService exe = Executors.newFixedThreadPool(20);
		try {
			Job105 job105 = new Job105();
			Job104 job104 = new Job104();
			Job103 job103 = new Job103();
			Job102 job102 = new Job102();
			Job101 job101 = new Job101();
			
			Pipeline pipeline = new Pipeline("pipeline1");
			pipeline.addJob(job103).addJob(job104);
			Layer layer1 = new Layer("layer1", exe);
			layer1.addJob(job101);
			
			Layer layer2 = new Layer("layer2", exe);
			layer2.addJob(job102).addJob(pipeline);
			
			Layer layer3 = new Layer("layer3", exe);
			layer3.addJob(job105);
			
			layer1.setNextLayer(layer2);
			layer2.setNextLayer(layer3);
			
			Executor executor = new Executor(layer1);
			JobContexts jobContexts = executor.execute();
			JobContext<?> context = jobContexts.findJobContext("105");
			Assert.assertTrue(context.getResult().equals("105 result"));
		} finally {
			exe.shutdown();
		}
	}
}
