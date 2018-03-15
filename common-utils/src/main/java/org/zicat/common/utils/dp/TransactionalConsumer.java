package org.zicat.common.utils.dp;

import org.zicat.common.utils.ds.queue.FileBlockingQueue;
import org.zicat.common.utils.io.IOUtils;

/**
 * 
 * @author lz31
 * @ThreadSafe
 * @param <E>
 */
public class TransactionalConsumer<E> extends Consumer<E> {

	protected FileBlockingQueue<E> blockingQueue;

	public TransactionalConsumer(FileBlockingQueue<E> blockingQueue, long consumerMaxIntervalTimeMillis, int consumerMaxCount, int threadCount) {

		super(blockingQueue, consumerMaxIntervalTimeMillis, consumerMaxCount, threadCount);
		this.blockingQueue = blockingQueue;
	}

	@Override
	protected void startCallBack() {

		super.startCallBack();
		blockingQueue.setTransaction();
	}

	@Override
	protected void consumeSuccess() throws Exception {

		try {
			super.consumeSuccess();
			blockingQueue.commit();
		} finally {
			blockingQueue.setTransaction();
		}
	}

	@Override
	protected void consumeFailure(Exception e) throws Exception {

		try {
			super.consumeFailure(e);
			blockingQueue.rollback();
		} finally {
			blockingQueue.setTransaction();
		}
	}

	@Override
	protected void closeCallBack() {
		try {
			super.closeCallBack();
		} finally {
			IOUtils.closeQuietly(blockingQueue);
		}
	}
}
