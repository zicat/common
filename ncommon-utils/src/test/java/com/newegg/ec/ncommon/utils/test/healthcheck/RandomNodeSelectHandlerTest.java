package com.newegg.ec.ncommon.utils.test.healthcheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.newegg.ec.ncommon.utils.healthcheck.RandomNodeSelectHandler;
import com.newegg.ec.ncommon.utils.test.healthcheck.TeskHealthCkecker.TestServerTransaction;

/**
 * 
 * @author lz31
 *
 */
public class RandomNodeSelectHandlerTest {
	
	@Test
	public void test() {
		
		List<TestServerTransaction> list = new ArrayList<>();
		int size = 2;
		for(int i = 0; i < size; i ++) {
			list.add(new TestServerTransaction(i).turnOn());
		}
		RandomNodeSelectHandler<TestServerTransaction, String> handler = new RandomNodeSelectHandler<>();
		TestServerTransaction t1 = handler.select(list, null);
		TestServerTransaction t2 = handler.select(list, null);
		TestServerTransaction t3 = handler.select(list, null);
		TestServerTransaction t4 = handler.select(list, null);
		TestServerTransaction t5 = handler.select(list, null);
		TestServerTransaction t6 = handler.select(list, null);
		
		Assert.assertNotNull(t1);
		Assert.assertNotNull(t2);
		Assert.assertNotNull(t3);
		Assert.assertNotNull(t4);
		Assert.assertNotNull(t5);
		Assert.assertNotNull(t6);
		
		Set<Integer> map = new HashSet<>();
		map.add(t1.getI());
		map.add(t2.getI());
		map.add(t3.getI());
		map.add(t4.getI());
		map.add(t5.getI());
		map.add(t6.getI());
		Assert.assertFalse(map.size() == 1);
	}
}
