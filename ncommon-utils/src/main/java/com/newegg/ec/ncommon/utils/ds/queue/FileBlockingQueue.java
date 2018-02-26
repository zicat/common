package com.newegg.ec.ncommon.utils.ds.queue;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.newegg.ec.ncommon.utils.io.IOUtils;

/**
 * @author lz31
 * @ThreadSafe
 * @param <E>
 */
public class FileBlockingQueue<E> extends AbstractQueue<E> implements Closeable, BlockingQueue<E> {
	
	final Charset UTF_8 = StandardCharsets.UTF_8;
	final SegmentFactory<E> segmentFactory;
	final LinkedBlockingDeque<Segment<E>> activeSegments = new LinkedBlockingDeque<>();
	final ConcurrentLinkedDeque<Segment<E>> transactionDeleteSegments = new ConcurrentLinkedDeque<>();
	final AtomicBoolean isTransaction = new AtomicBoolean(false);
	final RandomAccessFile idIndexFile;
	
	final ReentrantLock lock = new ReentrantLock(true);
	final Condition activeSegmentsIsEmpty = lock.newCondition();

	public FileBlockingQueue(SegmentFactory<E> segmentFactory) throws IOException {
		
		if(segmentFactory == null)
			throw new NullPointerException("segment factory is null");
		this.segmentFactory = segmentFactory;
		this.idIndexFile = new RandomAccessFile(segmentFactory.idFile, "rw");
		
		if(!segmentFactory.cleanUpOnStart)
			initActiveSegments();
	}

	/**
	 * init variable
	 * @throws IOException
     */
	private void initActiveSegments() throws IOException {
		
		if(idIndexFile.length() < 4)
			return;
		
		idIndexFile.seek(0);
		int size = idIndexFile.readInt();
		if(size <= 0)
			activeSegments.clear();
		
		for(int i = 0; i < size; i ++) {
			
			int length = idIndexFile.readInt();
			byte[] bs = new byte[length];
			int readLength = idIndexFile.read(bs);
			if(readLength != length)
				throw new IOException("read bytes error, want to read " + length + " bytes, but really read " + readLength + " bytes, file name is " + segmentFactory.idFile.getPath());

			String name = new String(bs, UTF_8);
			activeSegments.addLast(segmentFactory.createNewSegment(new File(segmentFactory.directory, name)));
		}
	}

	/**
	 * set transaction
	 */
	public void setTransaction() {
		
		final ReentrantLock lock = this.lock;
		final LinkedBlockingDeque<Segment<E>> activeSegments;
		lock.lock();
		try {
			isTransaction.set(true);
			activeSegments = this.activeSegments;
		} finally {
			lock.unlock();
		}

		for(Segment<E> seg: activeSegments) {
			seg.setTransaction();
		}
	}

	/**
	 * commit
	 * @throws IOException
     */
	public void commit() throws IOException {
		
		final ReentrantLock lock = this.lock;
		final Segment<E> currentReadSegment;
		lock.lock();
		try {
			if(isTransaction.get()) {
				isTransaction.set(false);
				currentReadSegment = activeSegments.peekFirst();
				if(currentReadSegment != null)
					currentReadSegment.commit();
				flush();
				for(Segment<E> seg: transactionDeleteSegments) {
					seg.closeAndDelete();
				}
				transactionDeleteSegments.clear();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * roll back
	 * @throws IOException
     */
	public void rollback() throws IOException {
		
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if(isTransaction.get()) {
				
				isTransaction.set(false);
				final Segment<E> currentReadSegment = activeSegments.peekFirst();
				if(currentReadSegment != null)
					currentReadSegment.rollback();
				for(Segment<E> seg: transactionDeleteSegments) {
					seg.rollback();
					activeSegments.addFirst(seg);
				}
				flush();
				if(!transactionDeleteSegments.isEmpty())
					activeSegmentsIsEmpty.signalAll();
				
				transactionDeleteSegments.clear();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * read data
	 * @return
	 * @throws IOException
     */
	private E read() throws IOException {
		
		final Segment<E> currentReadSegment;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			currentReadSegment = activeSegments.peekFirst();
			if(currentReadSegment == null)
				return null;
		} finally {
			lock.unlock();
		}
		
		E element = currentReadSegment.read();
		lock.lock();
		try {
			if(element == null && currentReadSegment.isReadFileTail()) {
				activeSegments.remove(currentReadSegment);
				if(isTransaction.get()) {
					transactionDeleteSegments.addFirst(currentReadSegment);
				} else {
                    flush();
					currentReadSegment.closeAndDelete();
				}
			}
		} finally {
			lock.unlock();
		}
		
		if(element == null && currentReadSegment.isReadFileTail()) {
			return read();
		} else {
			return element;
		}
	}

	/**
	 * read data blocking
	 * timeout < 0 then blocking until readable
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
     */
	private E read(long timeout, TimeUnit unit) throws IOException, InterruptedException {
		
		Segment<E> currentReadSegment = null;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			currentReadSegment = activeSegments.peek();
			if(currentReadSegment == null) {
				if(timeout > 0) {
					activeSegmentsIsEmpty.await(timeout, unit);
				} else {
					activeSegmentsIsEmpty.await();
				}
				currentReadSegment = activeSegments.peek();
				if(currentReadSegment == null)
					return null;
			}
			
		} finally {
			lock.unlock();
		}
		
		E element = currentReadSegment.read(timeout, unit);
		
		lock.lock();
		try {
			if(element == null && currentReadSegment.isReadFileTail()) {
				activeSegments.remove(currentReadSegment);
				if(isTransaction.get()) {
					if(!transactionDeleteSegments.contains(currentReadSegment))
						transactionDeleteSegments.addFirst(currentReadSegment);
				} else {
                    flush();
					currentReadSegment.closeAndDelete();
				}
			}
		} finally {
			lock.unlock();
		}
		
		if(element == null && currentReadSegment.isReadFileTail()) {
			return read(timeout, unit);
		} else {
			return element;
		}
	}

	/**
	 * create new segment
	 * @param old
	 * @throws IOException
     */
	private boolean createNewSegment(final Segment<E> old) throws IOException {
		
		if (old == activeSegments.peekLast()) {
			Segment<E> currentWriteSegment = segmentFactory.createNewSegment();
			activeSegments.addLast(currentWriteSegment);
			if (isTransaction.get()) {
				currentWriteSegment.setTransaction();
			}
			flush();
			return true;
		}
		return false;
	}

	/**
	 * add element
	 * @param element
	 * @return
	 * @throws IOException
     */
	private boolean add0(E element) throws IOException {
		
		final ReentrantLock lock = this.lock;
		lock.lock();
		Segment<E> currentWriteSegment = activeSegments.peekLast();
		try {
			while(currentWriteSegment == null) {
				createNewSegment(currentWriteSegment);
				currentWriteSegment = activeSegments.peekLast();
			}
			activeSegmentsIsEmpty.signalAll();
		} finally {
			lock.unlock();
		}

		final boolean success = currentWriteSegment.add(element);
		
		if(success)
			return success;
		
		lock.lock();
		try {
			//if return false, another thread have been created the new segment and that thread will be signal.
			if(createNewSegment(currentWriteSegment))
				activeSegmentsIsEmpty.signalAll();
		} finally {
			lock.unlock();
		}
		return add0(element);
	}

	/**
	 * close file blocking ququq
	 * @throws IOException
     */
	@Override
	public void close() throws IOException {
		
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			for(Segment<E> seg: activeSegments) {
				IOUtils.closeQuietly(seg);
			}
			
			activeSegments.clear();
			try {
				idIndexFile.getFD().sync();
			} finally {
				IOUtils.closeQuietly(idIndexFile);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * add element
	 * @param element
	 * @return
     */
	@Override
	public boolean add(E element) {
		
		try {
			return add0(element);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * get element
	 * @param e
	 * @return
     */
	@Override
	public boolean offer(E e) {
		return add(e); //add method never block and return false
	}

	/**
	 * get element
	 * @param e
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
     */
	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		return add(e);//add method never block and return false
	}

	/**
	 * add element
	 * @param e
	 * @throws InterruptedException
     */
	@Override
	public void put(E e) throws InterruptedException {
		add(e); //add method never block and return false
	}

	/**
	 * get element
	 * @return
     */
	@Override
	public E poll() {
		try {
			return read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * take element, if empty blocking
	 * @return
	 * @throws InterruptedException
     */
	@Override
	public E take() throws InterruptedException {
		try {
			return read(0, null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * poll element, blocking when empty
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
     */
	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		try {
			return read(timeout, unit);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * flush
	 * @throws IOException
     */
	private void flush() throws IOException {
		
		int size = 4;
		List<byte[]> segByteList = new ArrayList<>(activeSegments.size());
		for(Segment<E> seg: activeSegments) {
			byte[] bs = seg.getId().getBytes(UTF_8);
			segByteList.add(bs);
			size += 4;
			size += bs.length;
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream(size);
		for(int i = 0; i < 4; i++) {
			os.write((byte)((activeSegments.size() >>> (24 - 8 * i)) & 0xFF));
		}

		for(byte[] bs: segByteList) {

			for(int i = 0; i < 4; i++) {
				os.write((byte)((bs.length >>> (24 - 8 * i)) & 0xFF));
			}
			os.write(bs);
		}
		idIndexFile.seek(0);
		idIndexFile.write(os.toByteArray());
	}

	@Override
	public E peek() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int remainingCapacity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		throw new UnsupportedOperationException();
	}
}
