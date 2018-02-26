package com.newegg.ec.ncommon.log.log4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.newegg.ec.ncommon.utils.dp.Consumer;
import com.newegg.ec.ncommon.utils.dp.ConsumerHandler;
import com.newegg.ec.ncommon.utils.dp.Producer;
import com.newegg.ec.ncommon.utils.dp.TransactionalConsumer;
import com.newegg.ec.ncommon.utils.ds.queue.FileBlockingQueue;
import com.newegg.ec.ncommon.utils.ds.queue.SegmentFactory;
import com.newegg.ec.ncommon.utils.ds.queue.SerializableHandler;
import com.newegg.ec.ncommon.utils.io.IOUtils;

/**
 * async appender by file blocking queue
 * @author lz31
 *
 */
public abstract class AsyncAppenderSkeleton extends AppenderSkeleton implements ConsumerHandler<LoggingEvent> {
	
	protected Producer<LoggingEvent> producer;
	protected Consumer<LoggingEvent> consumer;
	protected FileBlockingQueue<LoggingEvent> fileBlockingQueue;
	
	private String filePath;
	private int segmentSize = 1024 * 1024 * 1;
	private int consumerMaxIntervalTimeMillis = 5000;
	private int consumerMaxCount = 50;
	private int threadCount = 1;
	
	@Override
    public void activateOptions() {
		
		try {
			super.activateOptions();
			boolean cleanUpOnStart = filePath == null || filePath.isEmpty();
			File file = cleanUpOnStart? File.createTempFile("queue", ".mg"): new File(filePath).getCanonicalFile();
			SegmentFactory<LoggingEvent> factory = new SegmentFactory<>(file, segmentSize, createSerializableHandler(), cleanUpOnStart);
			fileBlockingQueue = new FileBlockingQueue<>(factory);
			producer = new Producer<>(fileBlockingQueue);
			consumer = new TransactionalConsumer<>(fileBlockingQueue, consumerMaxIntervalTimeMillis, consumerMaxCount, threadCount);
			consumer.start(this);
		} catch (IOException e) {
			close();
			throw new RuntimeException("init file blocking queue error", e);
		}
	}
	
	/**
	 * default implements, user can override this method to chose other serialize tools like google protobuf
	 * @return
	 */
	protected SerializableHandler<LoggingEvent> createSerializableHandler() {
		
		return new SerializableHandler<LoggingEvent>() {

			@Override
			public byte[] serialize(LoggingEvent e) throws IOException {
				
				ByteArrayOutputStream bo = null;
				ObjectOutputStream os = null;
				try {
					bo = new ByteArrayOutputStream();
					os = new ObjectOutputStream(bo);
					os.writeObject(e);
					return bo.toByteArray();
				} finally {
					IOUtils.closeQuietly(os, bo);
				}
			}

			@Override
			public LoggingEvent deserialize(byte[] bs) throws IOException {
				
				ByteArrayInputStream bi = null;
				ObjectInputStream oi = null;
				try {
					bi = new ByteArrayInputStream(bs);
					oi = new ObjectInputStream(bi);
					Object result = oi.readObject();
					return (LoggingEvent) result;
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				} finally {
					IOUtils.closeQuietly(oi, bi);
				}
			}
		};
	}

	@Override
	public void append(LoggingEvent event) {
		producer.product(event);
	}


	@Override
	public void close() {
		try {
			try {
				if(consumer != null) {
					consumer.close();
					consumer = null;
				}
			} finally {
				if(fileBlockingQueue != null) {
					fileBlockingQueue.close();
					fileBlockingQueue = null;
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getSegmentSize() {
		return segmentSize;
	}

	public void setSegmentSize(int segmentSize) {
		this.segmentSize = segmentSize;
	}

	public int getConsumerMaxIntervalTimeMillis() {
		return consumerMaxIntervalTimeMillis;
	}

	public void setConsumerMaxIntervalTimeMillis(int consumerMaxIntervalTimeMillis) {
		this.consumerMaxIntervalTimeMillis = consumerMaxIntervalTimeMillis;
	}

	public int getConsumerMaxCount() {
		return consumerMaxCount;
	}

	public void setConsumerMaxCount(int consumerMaxCount) {
		this.consumerMaxCount = consumerMaxCount;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
}
