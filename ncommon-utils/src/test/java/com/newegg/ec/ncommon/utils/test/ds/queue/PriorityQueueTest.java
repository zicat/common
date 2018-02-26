package com.newegg.ec.ncommon.utils.test.ds.queue;

import org.junit.Test;

import com.newegg.ec.ncommon.utils.ds.queue.PriorityQueue;

import junit.framework.Assert;

public class PriorityQueueTest {
	
	@Test
	public void test() {
				
	  	PriorityQueue<Integer> queue = new PriorityQueue<Integer>(4, false) {

			@Override
			protected boolean lessThan(Integer a, Integer b) {
				return a < b;
			}
		};
		queue.insertWithOverflow(7);
		queue.insertWithOverflow(2);
		queue.insertWithOverflow(4);
		queue.insertWithOverflow(6);
		queue.insertWithOverflow(0);
		queue.insertWithOverflow(9);
		queue.insertWithOverflow(4);
		queue.insertWithOverflow(100);
		queue.insertWithOverflow(3);
		queue.insertWithOverflow(18);
		queue.insertWithOverflow(17);
		queue.insertWithOverflow(6);
		queue.insertWithOverflow(18);
		queue.insertWithOverflow(7);
		
		Assert.assertEquals(queue.pop(), Integer.valueOf(17));
		Assert.assertEquals(queue.pop(), Integer.valueOf(18));
		Assert.assertEquals(queue.pop(), Integer.valueOf(18));
		Assert.assertEquals(queue.pop(), Integer.valueOf(100));
		Assert.assertEquals(queue.pop(), null);
	}
}
