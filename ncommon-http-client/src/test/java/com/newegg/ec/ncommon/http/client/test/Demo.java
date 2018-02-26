package com.newegg.ec.ncommon.http.client.test;

import com.newegg.ec.ncommon.http.client.RestfullClient;
import com.newegg.ec.ncommon.http.client.factory.jersey.DefaultJerseyClientFactory;
import com.newegg.ec.ncommon.http.client.factory.jersey.GrizzlyJerseyClientFactory;

public class Demo {
	
	public static void main(String[] args) throws Exception {
		
		final DefaultJerseyClientFactory factory = new GrizzlyJerseyClientFactory(100, 100, 100);
		Thread t1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					RestfullClient restfullClient = new RestfullClient(factory);
					System.out.println(restfullClient.getAsync("http://10.16.46.197:8600/spellcheck", "/rest/v1/ping", null, null).get());
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		});
		
		Thread t2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					RestfullClient restfullClient = new RestfullClient(factory);
					System.out.println(restfullClient.getAsync("http://10.16.46.197:8600/spellcheck", "/rest/v1/ping", null, null).get());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Thread t3 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				factory.setReadTimeout(2000);
				factory.setConnectionTimeout(2000);
				factory.reload();
			}
		});
		
		t1.start();
		t2.start();
		t3.start();
		t1.join();
		t2.join();
		t3.join();
		System.out.println("request finished");
		factory.close();
	}

}
