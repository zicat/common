package com.newegg.ec.ncommon.http.client.test;

import com.newegg.ec.ncommon.http.client.RestfullClient;
import com.newegg.ec.ncommon.http.client.factory.AbstractClientFactory;
import com.newegg.ec.ncommon.http.client.factory.jersey.DefaultJerseyClientFactory;
import com.newegg.ec.ncommon.http.client.factory.jersey.GrizzlyJerseyClientFactory;
import com.newegg.ec.ncommon.http.client.factory.jersey.JettyJerseyClientFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 
 * @author lz31
 *
 */
public class Main {
	
	public static void main(String[] args) throws Exception {
		
		AbstractClientFactory grizzlyFactory = new GrizzlyJerseyClientFactory(100, 10000, 10000);
		AbstractClientFactory jettyFactory = new JettyJerseyClientFactory(100, 10000, 10000);
		AbstractClientFactory defaultFactory = new DefaultJerseyClientFactory(100, 10000, 10000);
		/** 
		testOneThread(grizzlyFactory);
		testOneThread(jettyFactory);
		testOneThread(defaultFactory);**/
		/**
		 * 
		Multi Thread*/
		
		multiThreadRun(grizzlyFactory);
		multiThreadRun(jettyFactory);
		multiThreadRun(defaultFactory);
		grizzlyFactory.close();
		jettyFactory.close();
		defaultFactory.close();
	}

	public static void multiThreadRun(AbstractClientFactory factory) throws InterruptedException {
		
		List<Thread> threads = new ArrayList<>(20);
		final AtomicLong spends = new AtomicLong(0L);
		for(int i = 0; i < 50; i ++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						long start = System.currentTimeMillis();
						for(int i = 0; i < 100; i ++) {
							RestfullClient client = new RestfullClient(factory);
							client.get("http://10.16.46.197:8600/spellcheck", "/rest/v1/ping", null, null);
						}
						spends.getAndAdd(System.currentTimeMillis() - start);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
			thread.start();
			threads.add(thread);
		}
		
		long start = System.currentTimeMillis();
		for(Thread thread: threads) {
			thread.join();
		}
		System.out.println(System.currentTimeMillis() - start);
	}
	
	public static void testOneThread(AbstractClientFactory factory) throws Exception {
		
		long start = System.currentTimeMillis();
		
		for(int i = 0; i < 2000; i ++) {
			RestfullClient client = new RestfullClient(factory);
			client.get("http://10.16.46.197:8600/spellcheck", "/rest/v1/ping", null, null);
		}
		System.out.println("Spend(2000 request):" + (System.currentTimeMillis() - start));
	}
}
