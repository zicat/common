package org.zicat.common.utils.dp;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.zicat.common.utils.ds.queue.FileBlockingQueue;

/**
 * 
 * @author lz31
 *
 * @param <E>
 */
public class FastTransactionalConsumer<E> extends TransactionalConsumer<E> {

	private long failureSleep;

	public FastTransactionalConsumer(FileBlockingQueue<E> blockingQueue, int consumerMaxCount, int threadCount) {
		super(blockingQueue, 1, consumerMaxCount, threadCount);
		this.failureSleep = sleepTime * 3;
	}

	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	protected int poll(List<E> container) throws InterruptedException {

		E e = null;
		int count = 0;

		while ((e = blockingQueue.poll(consumerMaxIntervalTimeMillis, TimeUnit.MILLISECONDS)) != null) {
			container.add(e);
			count++;
			if (container.size() >= consumerMaxCount)
				break;
		}
		return count;
	}

	/**
	 * 
	 */
	protected void failureSleep() throws InterruptedException {
		Thread.sleep(failureSleep);
	}
}
