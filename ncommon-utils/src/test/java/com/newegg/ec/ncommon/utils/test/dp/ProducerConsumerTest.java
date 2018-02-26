package com.newegg.ec.ncommon.utils.test.dp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.newegg.ec.ncommon.utils.dp.Consumer;
import com.newegg.ec.ncommon.utils.dp.ConsumerHandler;
import com.newegg.ec.ncommon.utils.dp.FastTransactionalConsumer;
import com.newegg.ec.ncommon.utils.dp.Producer;
import com.newegg.ec.ncommon.utils.ds.queue.FileBlockingQueue;
import com.newegg.ec.ncommon.utils.ds.queue.SegmentFactory;
import com.newegg.ec.ncommon.utils.ds.queue.StringSerializableHandler;

public class ProducerConsumerTest {
	
	@Test
	public void test() throws IOException, InterruptedException {
		
		test(3312, 1);
		System.out.println("==================");
		test(332, 10);
	}
	
	public void test(final int total, final int threadCount) throws IOException, InterruptedException {
		
		SegmentFactory<String> factory = new SegmentFactory<>(File.createTempFile("aaa", "txt"), 1024 * 1024 * 500, new StringSerializableHandler(), true);
		final FileBlockingQueue<String> queue = new FileBlockingQueue<>(factory);
		
		Producer<String> producer = new Producer<>(queue);
		
		Consumer<String> consumer = new FastTransactionalConsumer<>(queue, 100, threadCount);
		final AtomicInteger offset = new AtomicInteger(0);
		
		consumer.start(new ConsumerHandler<String>() {
			
			@Override
			public void dealException(List<String> elements, Exception e) {
				System.out.println(Thread.currentThread() + ":" + elements.size());
			}
			
			@Override
			public void consume(List<String> elements) throws Exception {
				for(String e: elements) {
					if(threadCount == 1)
						Assert.assertEquals(Integer.valueOf(offset.get()), Integer.valueOf(e));
					offset.incrementAndGet();
				}
				throw new RuntimeException();
			}
		});
		
		for(int i = 0; i < total; i ++) {
			producer.product(i + "");
		}
		consumer.close();
		Assert.assertEquals(offset.get(), total);
	}
}
