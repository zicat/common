package com.newegg.ec.ncommon.utils.test.ds.queue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.newegg.ec.ncommon.utils.ds.queue.FileBlockingQueue;
import com.newegg.ec.ncommon.utils.ds.queue.SegmentFactory;
import com.newegg.ec.ncommon.utils.ds.queue.StringSerializableHandler;

public class FileBlockingQueueExample {
	
	@Test
	public void transactionExample() throws IOException, InterruptedException {
		
		File file = createFile("D:\\aa.que");
		
		//Param:cleanUpOnStart if load remaining data when restart use "false" 
		SegmentFactory<String> factory = new SegmentFactory<>(file, 1024 * 1024 * 500, new StringSerializableHandler(), true);
		
		final FileBlockingQueue<String> queue = new FileBlockingQueue<>(factory);
		
		ReadThread read = new ReadThread(queue);
		WriteThread write = new WriteThread(queue);
		read.start();
		write.start();
		
		Thread.sleep(1000);
		read.close();
		write.close();
		
	}
	
	public static class ReadThread extends Thread {
		
		private volatile boolean closed = false;
		private final FileBlockingQueue<String> queue;
		
		
		private final int bufferSize = 100;
		private final List<String> buffer = new ArrayList<>(bufferSize);
		
		public ReadThread(final FileBlockingQueue<String> queue) {
			this.queue = queue;
		}
		
		@Override
		public void run() {
			
			queue.setTransaction();
			while(!closed) {
				try {
					String result = queue.poll(10, TimeUnit.MILLISECONDS);
					if(result != null) {
						buffer.add(result);
					}
					
					if(buffer.size() >= bufferSize) {
						consumer(buffer);
						buffer.clear();
						queue.commit();
						queue.setTransaction();
					}
						
				} catch (InterruptedException e) {
					break;
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}
		
		//Use thread pool consumer buffer data
		public void consumer(List<String> buffer) {
			
			for(String s: buffer) {
				System.out.println(s);
			}
		}
		
		public void close() {
			closed = true;
		}
	}
	
	public static class WriteThread extends Thread {
		
		private volatile boolean closed = false;
		private int count = 0;
		private final FileBlockingQueue<String> queue;
		
		public WriteThread(final FileBlockingQueue<String> queue) {
			this.queue = queue;
		}
		
		@Override
		public void run() {
			
			while(!closed) {
				for(int i = 0; i < 10000; i ++) {
					queue.add("write data, id = " + count++);
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		
		public void close() {
			closed = true;
		}
	}
	
	private File createFile(String fileName) throws IOException {
		
		File file = new File(fileName);
		return file;
	}
}
