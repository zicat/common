package com.newegg.ec.ncommon.utils.test.ds.queue;

import com.newegg.ec.ncommon.utils.ds.queue.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by lz31 on 2016/9/7.
 */
public class TestFileBlockingQueue {

	@Test
	public void performanceTest() throws IOException, InterruptedException {

		File file = File.createTempFile("test", ".txt");
		SegmentFactory<String> factory = new SegmentFactory<>(file, 1024 * 1024 * 500, new StringSerializableHandler(), true);
		final FileBlockingQueue<String> queue = new FileBlockingQueue<>(factory);
		try {
			final String test = "asdfsdadsfdsfasdfasdkfhasdfkjahsdkfhkxzhvkzxhvkzxhvkzxhcvkahsdfhaksfhadskjfhakdsfhadksfhaksdfhaksdfhakdsfhkadfhakdsfhadksfhkdsafhaksdfhksd" +
					"adsfasfasdfkahsfalsdjflaksdfjlasdjflasjflasdjflajsdflasdjflasjfladsjfl12iuodsafiusdf7as9fpasdfasd'fsad'fds'fjsfj'asdfj'saldfjlsdkfjlsafjfh";
			Thread write = new Thread(new Runnable() {
				@Override
				public void run() {
					
					long start = System.currentTimeMillis();
					for(int i = 0; i < 100000; i ++) {
						queue.add(test);
					}
					System.out.println("write 100000 string element for " + (System.currentTimeMillis() - start)+ " ms");
				}
			});
			
			Thread read = new Thread(new Runnable() {
				@Override
				public void run() {
					
					String result = null;
					try {
						int count = 0;
						while((result = queue.poll(2, TimeUnit.SECONDS)) != null) {
							Assert.assertEquals(result, test);
							count ++;
						}
						Assert.assertTrue(count == 100000);
						System.out.println("total read string element " + count);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			write.start();
			read.start();
			write.join();
			read.join();
		} finally {
			queue.close();
		}
	}

	@Test
	public void testTransaction() throws IOException, InterruptedException {

		String s = "abcabcabcabc";
		String halfS = "zxcvbn";
		String halfMoreS = "qwertya";
		String halfLessS = "uiop,";
		String lessS = "nm,jkuil.;p";

		SerializableHandler<String> handler = new StringSerializableHandler();
		File file = File.createTempFile("test", ".txt");
		SegmentFactory<String> segmentFactory = new SegmentFactory<>(file, Segment.elementWriteLength(handler.serialize(s)) + Segment.HEAD_END_POINT, handler, true);
		FileBlockingQueue<String> fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		try {
			Assert.assertTrue(fileBlockingQueue.add(s));
			Assert.assertTrue(fileBlockingQueue.add(halfS));
			Assert.assertTrue(fileBlockingQueue.add(halfMoreS));
			Assert.assertTrue(fileBlockingQueue.add(halfLessS));
			Assert.assertTrue(fileBlockingQueue.add(lessS));

			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), s);
			fileBlockingQueue.rollback();

			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), s);
			Assert.assertEquals(fileBlockingQueue.poll(), halfS);
			fileBlockingQueue.rollback();

			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), s);
			Assert.assertEquals(fileBlockingQueue.poll(), halfS);
			Assert.assertEquals(fileBlockingQueue.poll(), halfMoreS);
			fileBlockingQueue.rollback();

			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), s);
			Assert.assertEquals(fileBlockingQueue.poll(), halfS);
			Assert.assertEquals(fileBlockingQueue.poll(), halfMoreS);
			Assert.assertEquals(fileBlockingQueue.poll(), halfLessS);
			fileBlockingQueue.rollback();

			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), s);
			Assert.assertEquals(fileBlockingQueue.poll(), halfS);
			Assert.assertEquals(fileBlockingQueue.poll(), halfMoreS);
			Assert.assertEquals(fileBlockingQueue.poll(), halfLessS);
			Assert.assertEquals(fileBlockingQueue.poll(), lessS);
			fileBlockingQueue.rollback();

			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), s);
			fileBlockingQueue.commit();

			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), halfS);
			fileBlockingQueue.commit();

			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), halfMoreS);
			fileBlockingQueue.commit();

			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), halfLessS);
			fileBlockingQueue.commit();

			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), lessS);
			fileBlockingQueue.commit();

			Assert.assertTrue(fileBlockingQueue.add(s));
			Assert.assertTrue(fileBlockingQueue.add(halfS));
			Assert.assertTrue(fileBlockingQueue.add(halfMoreS));
			Assert.assertTrue(fileBlockingQueue.add(halfLessS));
			Assert.assertTrue(fileBlockingQueue.add(lessS));
			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), s);
			Assert.assertEquals(fileBlockingQueue.poll(), halfS);
			Assert.assertEquals(fileBlockingQueue.poll(), halfMoreS);
			Assert.assertEquals(fileBlockingQueue.poll(), halfLessS);
			fileBlockingQueue.commit();
			Assert.assertEquals(fileBlockingQueue.poll(), lessS);
		} finally {
			fileBlockingQueue.close();
		}

		segmentFactory = new SegmentFactory<>(file, Segment.elementWriteLength(handler.serialize(s)) + Segment.HEAD_END_POINT, handler, true);
		fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		try {
			Assert.assertTrue(fileBlockingQueue.add(s));
			Assert.assertTrue(fileBlockingQueue.add(halfS));
			Assert.assertTrue(fileBlockingQueue.add(halfMoreS));
			Assert.assertTrue(fileBlockingQueue.add(halfLessS));
			Assert.assertTrue(fileBlockingQueue.add(lessS));

			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), s);
			Assert.assertEquals(fileBlockingQueue.poll(), halfS);
			Assert.assertEquals(fileBlockingQueue.poll(), halfMoreS);
		} finally {
			fileBlockingQueue.close();
		}


		segmentFactory = new SegmentFactory<>(file, Segment.elementWriteLength(handler.serialize(s)) + Segment.HEAD_END_POINT, handler, false);
		fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		try {
			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), s);
			Assert.assertEquals(fileBlockingQueue.poll(), halfS);
			Assert.assertEquals(fileBlockingQueue.poll(), halfMoreS);
			fileBlockingQueue.commit();
		} finally {
			fileBlockingQueue.close();
		}

		segmentFactory = new SegmentFactory<>(file, Segment.elementWriteLength(handler.serialize(s)) + Segment.HEAD_END_POINT, handler, false);
		fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		try {
			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), halfLessS);
			fileBlockingQueue.rollback();
		} finally {
			fileBlockingQueue.close();
		}

		segmentFactory = new SegmentFactory<>(file, Segment.elementWriteLength(handler.serialize(s)) + Segment.HEAD_END_POINT, handler, false);
		fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		try {
			fileBlockingQueue.setTransaction();
			Assert.assertEquals(fileBlockingQueue.poll(), halfLessS);
			fileBlockingQueue.commit();
		} finally {
			fileBlockingQueue.close();
		}

		segmentFactory = new SegmentFactory<>(file, Segment.elementWriteLength(handler.serialize(s)) + Segment.HEAD_END_POINT, handler, false);
		fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		try {
			Assert.assertEquals(fileBlockingQueue.poll(), lessS);
			Assert.assertNull(fileBlockingQueue.poll());
		} finally {
			fileBlockingQueue.close();
		}

	}

	@Test
	public void mulitThreadTransactionTest() throws IOException, InterruptedException {

		SerializableHandler<String> handler = new StringSerializableHandler();
		SegmentFactory<String> segmentFactory = new SegmentFactory<>(File.createTempFile("test", ".txt"), 30, handler, true);
		final FileBlockingQueue<String> fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		final ExecutorService service  = Executors.newFixedThreadPool(10);
		final int count = 100;
		final String suffix = "abcde";

		Thread read = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Set<String> valueSet = new HashSet<>();
					for(int i = 0; i < count; i ++) {
						valueSet.add(i + suffix);
					}

					fileBlockingQueue.setTransaction();
					int i = 0;
					boolean hasRollback = false;
					Set<String> rollbackSet = new HashSet<>();
					while(!valueSet.isEmpty() || !rollbackSet.isEmpty()) {
						String v = fileBlockingQueue.take();
						Assert.assertTrue( (!hasRollback && valueSet.contains(v)) || (hasRollback && (valueSet.contains(v) || rollbackSet.contains(v))));
						valueSet.remove(v);
						if(hasRollback) {
							rollbackSet.remove(v);
						} else {
							rollbackSet.add(v);
						}
						i++;
						if(i == count / 2) {
							fileBlockingQueue.rollback();
							hasRollback = true;
							fileBlockingQueue.setTransaction();
						}
					}
					fileBlockingQueue.commit();
					Assert.assertNull(fileBlockingQueue.poll());
				} catch (Throwable e) {
					Assert.assertTrue(false);
				}
			}
		});

		Thread write = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ignore) {}

				for(int i = 0; i < count; i ++) {

					final int j = i;
					service.submit(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return fileBlockingQueue.add(j + suffix);
						}
					});
				}
			}
		});

		read.start();
		write.start();
		write.join();
		read.join();
		fileBlockingQueue.close();
		service.shutdown();
	}

	@Test
	public void multiThreadTest() throws IOException, InterruptedException {

		File file = File.createTempFile("test", ".txt");
		SerializableHandler<String> handler = new StringSerializableHandler();
		SegmentFactory<String> segmentFactory = new SegmentFactory<>(file, 30, handler, true);
		final FileBlockingQueue<String> fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		ExecutorService writePool  = Executors.newFixedThreadPool(10);
		ExecutorService readPool = Executors.newFixedThreadPool(10);
		int count = 1000;
		final String suffix = "abc12de";
		try {
			for(int i = 0; i < count; i ++) {

				final int j = i;
				writePool.submit(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return fileBlockingQueue.add(j + suffix);
					}
				});
			}

			final Set<String> valueSet = new ConcurrentSkipListSet<>();
			for(int i = 0; i < count; i ++) {
				valueSet.add(i + suffix);
			}

			List<Future<Integer>> fs = new ArrayList<>();
			for(int i = 0; i < 10; i ++) {
				fs.add(readPool.submit(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {

						while(!valueSet.isEmpty() ) {
							String v = fileBlockingQueue.poll(10, TimeUnit.MILLISECONDS);
							if(v != null) {
								Assert.assertTrue(valueSet.contains(v));
								valueSet.remove(v);
							}
						}
						return 1;
					}
				}));
			}

			for(Future<Integer> f: fs) {
				f.get();
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			fileBlockingQueue.close();
			writePool.shutdown();
			readPool.shutdown();
		}
	}

	@Test
	public void testLength() throws IOException, InterruptedException {

		String s = "abcabcabcabc";
		String halfS = "zxcvbn";
		String halfMoreS = "qwertya";
		String halfLessS = "uiop,";
		String lessS = "nm,jkuil.;p";
		String moreS = "aaaaaaaaaaaaa";

		SerializableHandler<String> handler = new StringSerializableHandler();
		File file = File.createTempFile("test", ".txt");
		SegmentFactory<String> segmentFactory = new SegmentFactory<>(file, Segment.elementWriteLength(handler.serialize(s)) + Segment.HEAD_END_POINT, handler, true);
		FileBlockingQueue<String> fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		try {
			Assert.assertTrue(fileBlockingQueue.add(s));
			Assert.assertTrue(fileBlockingQueue.add(halfS));
			Assert.assertTrue(fileBlockingQueue.add(halfMoreS));
			Assert.assertTrue(fileBlockingQueue.add(halfLessS));
			Assert.assertTrue(fileBlockingQueue.add(lessS));
			try {
				fileBlockingQueue.add(moreS);
				Assert.assertTrue(false);
			} catch (Throwable e) {
				Assert.assertTrue(true);
			}
			Assert.assertEquals(fileBlockingQueue.poll(), s);
			Assert.assertEquals(fileBlockingQueue.take(), halfS);

			int waitTime = 1000;
			long start = System.currentTimeMillis();
			Assert.assertEquals(fileBlockingQueue.poll(waitTime, TimeUnit.MILLISECONDS), halfMoreS);
			Assert.assertEquals(fileBlockingQueue.poll(waitTime, TimeUnit.MILLISECONDS), halfLessS);
			Assert.assertEquals(fileBlockingQueue.poll(waitTime, TimeUnit.MILLISECONDS), lessS);
			long spend = System.currentTimeMillis() - start;
			Assert.assertTrue(spend < waitTime);

			Assert.assertNull(fileBlockingQueue.poll());

			start = System.currentTimeMillis();
			Assert.assertNull(fileBlockingQueue.poll(waitTime, TimeUnit.MILLISECONDS));
			spend = System.currentTimeMillis() - start;
			Assert.assertTrue(spend >= waitTime);
			//Assert.assertNull(fileBlockingQueue.take());
		} finally {
			fileBlockingQueue.close();
		}

		segmentFactory = new SegmentFactory<>(file, Segment.elementWriteLength(handler.serialize(s)) + Segment.HEAD_END_POINT, handler, true);
		fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		try {
			Assert.assertTrue(fileBlockingQueue.add(s));
			Assert.assertTrue(fileBlockingQueue.add(halfS));
			Assert.assertTrue(fileBlockingQueue.add(halfMoreS));
			Assert.assertTrue(fileBlockingQueue.add(halfLessS));
			Assert.assertTrue(fileBlockingQueue.add(lessS));

			try {
				fileBlockingQueue.add(moreS);
				Assert.assertTrue(false);
			} catch (Throwable e) {
				Assert.assertTrue(true);
			}
			Assert.assertEquals(fileBlockingQueue.poll(), s);
			Assert.assertEquals(fileBlockingQueue.take(), halfS);
		} finally {
			fileBlockingQueue.close();
		}

		segmentFactory = new SegmentFactory<>(file, Segment.elementWriteLength(handler.serialize(s)) + Segment.HEAD_END_POINT, handler, false);
		fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		try {

			int waitTime = 1000;
			long start = System.currentTimeMillis();
			Assert.assertEquals(fileBlockingQueue.poll(waitTime, TimeUnit.MILLISECONDS), halfMoreS);
			Assert.assertEquals(fileBlockingQueue.poll(waitTime, TimeUnit.MILLISECONDS), halfLessS);
			Assert.assertEquals(fileBlockingQueue.poll(waitTime, TimeUnit.MILLISECONDS), lessS);
			long spend = System.currentTimeMillis() - start;
			Assert.assertTrue(spend < waitTime);

			Assert.assertNull(fileBlockingQueue.poll());

			start = System.currentTimeMillis();
			Assert.assertNull(fileBlockingQueue.poll(waitTime, TimeUnit.MILLISECONDS));
			spend = System.currentTimeMillis() - start;
			Assert.assertTrue(spend >= waitTime);
			//Assert.assertNull(fileBlockingQueue.take()); blocking
		} finally {
			fileBlockingQueue.close();
		}
	}

	@Test
	public void testTransaction2() throws IOException {

		String s = "abcabcabcabc";
		String s2 = "abcabcabca";
		SerializableHandler<String> handler = new StringSerializableHandler();
		File file = File.createTempFile("test", ".txt");
		SegmentFactory<String> segmentFactory = new SegmentFactory<>(file, Segment.elementWriteLength(handler.serialize(s)) + Segment.HEAD_END_POINT, handler, true);
		FileBlockingQueue<String> fileBlockingQueue = new FileBlockingQueue<>(segmentFactory);
		fileBlockingQueue.setTransaction();
		fileBlockingQueue.add(s2);
		fileBlockingQueue.add(s2);
		Assert.assertEquals(fileBlockingQueue.poll(), s2);
		Assert.assertEquals(fileBlockingQueue.poll(), s2);
		fileBlockingQueue.rollback();
		Assert.assertEquals(fileBlockingQueue.poll(), s2);
		Assert.assertEquals(fileBlockingQueue.poll(), s2);
		fileBlockingQueue.rollback();
		Assert.assertNull(fileBlockingQueue.poll());
		fileBlockingQueue.close();
	}
}
