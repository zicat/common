package com.newegg.ec.ncommon.utils.dp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author lz31
 * @ThreadSafe
 * @param <E>
 */
public class Producer<E> {
	
	protected final BlockingQueue<E> blockingQueue;
	
	public Producer(BlockingQueue<E> blockingQueue) {
		
		if(blockingQueue == null)
			throw new NullPointerException("blocking queue is null");
		
		this.blockingQueue = blockingQueue;
	}
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	public boolean product(E e) {
		
		if(e == null)
			return true;
		
		return blockingQueue.offer(e);
	}
	
	/**
	 * 
	 * @param e
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	public boolean product(E e, long timeout, TimeUnit unit) throws InterruptedException {
		
		if(e == null)
			return true;
		
		if(timeout <= 0) {
			blockingQueue.put(e);
			return true;
		} else {
			return blockingQueue.offer(e, timeout, unit);
		}
	}
}
