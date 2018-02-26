package org.zicat.common.utils.dp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author lz31
 * @ThreadSafe
 * @param <E>
 */
public class Consumer<E> {
	
	protected final BlockingQueue<E> blockingQueue;
	protected final long consumerMaxIntervalTimeMillis;
	protected final int consumerMaxCount;
	protected final int threadCount;
	protected final ExecutorService service;
	protected final long sleepTime;
	
	protected final AtomicBoolean started = new AtomicBoolean(false);
	protected final AtomicBoolean closed = new AtomicBoolean(false);
	protected final ReentrantLock lock = new ReentrantLock();
	protected final Condition closeCondition = lock.newCondition();
	
	public Consumer(BlockingQueue<E> blockingQueue, long consumerMaxIntervalTimeMillis, int consumerMaxCount, int threadCount) {
		
		if(blockingQueue == null)
			throw new NullPointerException("blocking queue is null");
		
		if(consumerMaxIntervalTimeMillis <= 0)
			throw new IllegalArgumentException("consumerMaxIntervalTimeMillis must more than 0");
		
		if(consumerMaxCount <= 0)
			throw new IllegalArgumentException("consumerMaxCount must more than 0");
		
		if(threadCount <= 0)
			throw new IllegalArgumentException("thread count must more than 0");
			
		
		this.blockingQueue = blockingQueue;
		this.consumerMaxIntervalTimeMillis = consumerMaxIntervalTimeMillis;
		this.consumerMaxCount = consumerMaxCount;
		this.threadCount = threadCount;
		this.service = Executors.newFixedThreadPool(threadCount + 1);
		this.sleepTime = consumerMaxIntervalTimeMillis == 1? 1: consumerMaxIntervalTimeMillis / 2;
	}
	
	/**
	 * 
	 * @param consumerHandler
	 */
	public void start(final ConsumerHandler<E> consumerHandler) {
		
		if(started.get())
			return;
		
		synchronized (this) {
			
			if(started.get())
				return;
			
			service.submit(new Runnable() {
				
				@Override
				public void run() {
					try {
						start0(consumerHandler);
					} catch (InterruptedException e) {}
				}
			});
			started.set(true);
		}
	}
	
	/**
	 * @throws InterruptedException 
	 * 
	 */
	public void close() throws InterruptedException {
		
		if(closed.get())
			return;
		
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if(closed.get())
				return;
			closed.set(true);
			closeCondition.await();
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 
	 * @param consumerHandler
	 * @throws InterruptedException
	 */
	private void start0(final ConsumerHandler<E> consumerHandler) throws InterruptedException {
		
		try {
			
			startCallBack();
			
			List<E> container = new ArrayList<>(consumerMaxCount);
			long spend = 0;
			boolean first = true;
			while(true) {
				
				long start = System.currentTimeMillis();
				int count = poll(container);
				if(!first) {
					spend += System.currentTimeMillis() - start;
				} else if(count > 0) {
					first = false;
				}
				
				// start consumer when (spend > consumerMaxIntervalTimeMillis|| container size more than consumerMaxCount) && container has value 
				if((spend >= consumerMaxIntervalTimeMillis || container.size() >= consumerMaxCount) && container.size() > 0) {
					consume(consumerHandler, container);
					container.clear();
					spend = 0;
				}
				
				if(closed.get() && count <= 0) { // product finish and consumer finish
					if(!container.isEmpty()) // last batch
						consume(consumerHandler, container);
					break;
				}
			}
		} finally {
			closeCallBack();
		}
	}
	
	/**
	 * 
	 * @param consumerHandler
	 * @param container
	 * @throws InterruptedException 
	 */
	private void consume(final ConsumerHandler<E> consumerHandler, List<E> container) throws InterruptedException {
		
		try {
			
			List<List<E>> subContainers = subList(container, threadCount);
			List<Future<?>> fs = new ArrayList<>(subContainers.size());
			for(final List<E> subContainer: subContainers) {
				
				if(subContainer != null && !subContainer.isEmpty()) {
					fs.add(service.submit(new Runnable() {
						
						@Override
						public void run() {
							try {
								consumerHandler.consume(subContainer);
							} catch (Exception e) {
								consumerHandler.dealException(subContainer, e);
							}
						}
					}));
				}
			}
			
			Exception allException = null;
			for(Future<?> f: fs) {
				try {
					f.get();
				} catch(Exception e) {
					if(allException == null) {
						allException = e;
					} else {
						allException.addSuppressed(e);
					}
				}
			}
			
			if(allException != null)
				throw allException;
			
			consumeSuccess();
			
		} catch(Exception e) {
			try {
				consumeFailure(e);
			} catch(Exception ignore) {}
			finally {
				failureSleep();
			}
		}
	}
	
	protected void failureSleep() throws InterruptedException {
		Thread.sleep(sleepTime);
	}
	
	/**
	 * 
	 */
	protected void closeCallBack() {
		
		ReentrantLock lock = this.lock;
		lock.lock();
		try {
			service.shutdown();
			closeCondition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 
	 */
	protected void startCallBack() {
		
	};
	
	/**
	 * 
	 * @throws Exception
	 */
	protected void consumeSuccess() throws Exception {
		
	}
	/**
	 * 
	 * @param e
	 */
	protected void consumeFailure(Exception e) throws Exception{
		
	}
	
	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	protected int poll(List<E> container) throws InterruptedException {
		E e = blockingQueue.poll(consumerMaxIntervalTimeMillis, TimeUnit.MILLISECONDS);
		if(e != null)
			container.add(e);
		return e == null?0: 1;
	}
	
	/**
	 * 
	 * @param list
	 * @param size
	 * @return
	 */
	protected List<List<E>> subList(List<E> list, int size) {
		
		if(list == null)
			throw new NullPointerException("list is null");
		
		List<List<E>> result = new ArrayList<>();
		if(size <= 1 || list.size() <= 1) {
			result.add(list);
			return result;
		}
		
		int batchSize = list.size() / size + 1;
		for(int i = 0; i < size; i++) {
			result.add(new ArrayList<E>(batchSize));
		}
		
		int i = 0;
		for(E e: list) {
			result.get(i % size).add(e);
			i++;
		}
		
		Iterator<List<E>> it = result.iterator();
		while(it.hasNext()) {
			if(it.next().isEmpty())
				it.remove();
		}
		return result;
	}
}
