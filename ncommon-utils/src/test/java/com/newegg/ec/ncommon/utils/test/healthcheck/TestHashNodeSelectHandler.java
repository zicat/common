package com.newegg.ec.ncommon.utils.test.healthcheck;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.newegg.ec.ncommon.utils.healthcheck.ConsistentHashNodeSelectHandler;
import com.newegg.ec.ncommon.utils.healthcheck.HashNodeSelectHandler;
import com.newegg.ec.ncommon.utils.healthcheck.NodeSelectHandler;
import com.newegg.ec.ncommon.utils.healthcheck.RandomNodeSelectHandler;

/**
 * 
 * @author lz31
 *
 */
public class TestHashNodeSelectHandler {
	
	@Test
	public void test() throws NoSuchAlgorithmException {
		
		List<String> nodes = new ArrayList<>();
		int serverCount = 3;
		ArrayList<Integer> count = new ArrayList<>();
		for(int i = 0; i < serverCount; i++) {
			nodes.add(i + "");
			count.add(i, 0);
		}
		
		long start = System.currentTimeMillis();
		int queryCount = 1000000;
		NodeSelectHandler<String, String> handler = new RandomNodeSelectHandler<>();
		NodeSelectHandler<String, String> handler2 = new HashNodeSelectHandler<>();
		NodeSelectHandler<String, String> handler3 = new ConsistentHashNodeSelectHandler<>(nodes);
		for(int i = 0; i < queryCount; i++) {
			String value = handler.select(nodes, i + "sasadfasdfdfas");
			handler2.select(nodes, i + "sasadfasdfdfas");
			handler3.select(nodes, i + "sasadfasdfdfas");
			int index = nodes.indexOf(value);
			Assert.assertTrue(index != -1);
			count.set(index, count.get(index) + 1);
		}
		System.out.println("spend " + (System.currentTimeMillis() - start));
		
		int avg = queryCount / serverCount ;
		for(Integer integer: count) {
			System.out.println(integer + ": " + Math.abs(avg - integer) * 100 / avg);
		}
	}
}
