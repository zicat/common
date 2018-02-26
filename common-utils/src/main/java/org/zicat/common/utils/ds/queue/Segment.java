package org.zicat.common.utils.ds.queue;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.zicat.common.utils.io.IOUtils;

/**
 * Segment File Format
 *
 * HEAD + BODY
 *
 * HEAD = 4Bytes(read position) + 4Bytes(write position) + 4Bytes(is write finish)
 * BODY = List of Elements
 *    Element = 1-5 Dynamic Byte(Element Length, VINT) + Length bytes(Element Value)
 *    
 * @author lz31
 * @ThreadSafe
 * @param <E>
 */

public class Segment<E> implements Closeable {

	public static final int HEAD_END_POINT = 12;
	public static final int WRITE_POSITION_START_POINT = 0;
	public static final int READ_POSITION_START_POINT = 4;
	public static final int IS_WRITE_FINISH_POSITION_START_POINT = 8;
	
	public static final int VINT_1_BYTE_LIMIT = (1 << 7);
	public static final int VINT_2_BYTE_LIMIT = (1 << 14);
	public static final int VINT_3_BYTE_LIMIT = (1 << 21);
	public static final int VINT_4_BYTE_LIMIT = (1 << 28);

	private final SerializableHandler<E> handler;
	private final RandomAccessFile segmentFileAccessor;
	private final FileChannel segmentFileChannel;
	private final File segmentFile;
	private final MappedByteBuffer writeBuffer;
	private final ByteBuffer indexBuffer;
	private final ByteBuffer readBuffer;
	private final ReentrantLock lock = new ReentrantLock(true);
	private final Condition readable = lock.newCondition();
	private final int length;

	private volatile int readPosition;
	private volatile int readTransactionPosition;
	private final AtomicBoolean isTransaction = new AtomicBoolean(false);
	private final AtomicBoolean isClose = new AtomicBoolean(false);

	private volatile int writePosition;
	private final AtomicBoolean isWriteFinish = new AtomicBoolean(false);

	/**
	 *
	 * @param segmentFile
	 * @param length
	 * @param handler
	 * @throws IOException
	 */
	public Segment(File segmentFile, int length, SerializableHandler<E> handler) throws IOException {

		if (segmentFile == null)
			throw new NullPointerException("segment is null");

		if (handler == null)
			throw new NullPointerException("serializable handler is null");

		this.segmentFile = segmentFile;
		this.length = length;
		this.handler = handler;
		this.segmentFileAccessor = new RandomAccessFile(segmentFile, "rw");
		this.segmentFileChannel = segmentFileAccessor.getChannel();
		this.writeBuffer = segmentFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, length);
		this.indexBuffer = writeBuffer.duplicate();
		this.readBuffer = writeBuffer.duplicate();
		this.writePosition = readBuffer.getInt();
		this.readPosition = readBuffer.getInt();
		this.isWriteFinish.set(readBuffer.getInt() == 1);

		if (writePosition == 0) {
			writeBuffer.position(HEAD_END_POINT);
			flushWritePosition(HEAD_END_POINT);
		} else {
			writeBuffer.position(writePosition);
		}

		if (readPosition == 0) {
			readBuffer.position(HEAD_END_POINT);
			flushReadPosition(HEAD_END_POINT);
		} else {
			readBuffer.position(readPosition);
		}
	}

	/**
	 * get segment id
	 * 
	 * @return
	 */
	public final String getId() {
		return segmentFile.getName();
	}

	/**
	 * flush read position to file head
	 * 
	 * @param readPosition
	 */
	private void flushReadPosition(int readPosition) {

		if (!isTransaction.get()) {
			this.indexBuffer.position(READ_POSITION_START_POINT);
			this.indexBuffer.putInt(readPosition);
		}
		this.readPosition = readPosition;
	}

	/**
	 * flush write position to file head
	 * 
	 * @param writePosition
	 */
	private void flushWritePosition(int writePosition) {

		this.writePosition = writePosition;
		this.indexBuffer.position(WRITE_POSITION_START_POINT);
		this.indexBuffer.putInt(writePosition);
	}

	/**
	 * check whether has remaining size
	 * 
	 * @param bs
	 * @return
	 */
	public boolean hasCapacity(byte[] bs) {
		return elementWriteLength(bs) + writePosition <= length;
	}

	/**
	 * get the size in segment file of the bs(length[4] + value[bs.length])
	 * 
	 * @param bs
	 * @return
	 */
	public static int elementWriteLength(byte[] bs) {

		int length = bs.length;
		if(length < VINT_1_BYTE_LIMIT) {
			return 1 + length;
		} else if(length < VINT_2_BYTE_LIMIT) {
			return 2 + length;
		} else if(length < VINT_3_BYTE_LIMIT) {
			return 3 + length;
		} else if(length < VINT_4_BYTE_LIMIT) {
			return 4 + length;
		}
		return 5 + length;
	}

	/**
	 *
	 * check whether the bs is more than the length
	 * 
	 * @param bs
	 * @return
	 */
	public boolean isValid(byte[] bs) {

		return elementWriteLength(bs) + HEAD_END_POINT <= length;
	}

	/**
	 * flush whether is write finish to head
	 */
	private void setWriteFinish() {

		isWriteFinish.set(true);
		indexBuffer.position(IS_WRITE_FINISH_POSITION_START_POINT);
		indexBuffer.putInt(1);
	}

	/**
	 * whether read in end
	 *
	 * @return
	 */
	public boolean isReadFileTail() {
		return isReadTail() && isWriteFinish.get();
	}

	/**
	 * whether is read position is more than write position
	 *
	 * @return
	 */
	public boolean isReadTail() {
		return readPosition >= writePosition;
	}

	/**
	 * set transaction
	 */
	public void setTransaction() {

		if (isClose.get())
			throw new IllegalStateException("Segment file closed");

		final Lock lock = this.lock;
		lock.lock();
		try {
			readTransactionPosition = readPosition;
			isTransaction.set(true);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * commit
	 */
	public void commit() {

		if (isClose.get())
			throw new IllegalStateException("Segment file closed");

		final Lock lock = this.lock;
		lock.lock();
		try {
			if (isTransaction.get()) {
				isTransaction.set(false);
				flushReadPosition(readPosition);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * rollback
	 */
	public void rollback() {

		if (isClose.get())
			throw new IllegalStateException("Segment file closed");

		final Lock lock = this.lock;
		lock.lock();
		try {
			if (isTransaction.get()) {
				isTransaction.set(false);
				flushReadPosition(readTransactionPosition);
				readBuffer.position(readTransactionPosition);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * add element to segment file
	 * 
	 * @param element
	 * @return
	 */
	public boolean add(E element) throws IOException {

		byte[] bytes = handler.serialize(element);
		if (bytes == null || bytes.length == 0)
			return true;

		if (!isValid(bytes)) {
			throw new RuntimeException("file length too small to add the element into file");
		}

		final Lock lock = this.lock;
		lock.lock();
		try {

			if (isClose.get())
				throw new IllegalStateException("Segment file closed");

			if (!hasCapacity(bytes)) {
				setWriteFinish();
				readable.signalAll();
				return false;
			}

			writeBuffer.put(buildBuffer(bytes));
			flushWritePosition(writeBuffer.position());
			readable.signal();
			return true;
		} finally {
			lock.unlock();
		}
	}

	private ByteBuffer buildBuffer(byte[] bytes) {

		ByteBuffer buffer = ByteBuffer.allocate(elementWriteLength(bytes));
		int length = bytes.length;
		while ((length & ~0x7F) != 0) {
			buffer = buffer.put((byte) ((length & 0x7f) | 0x80));
			length >>>= 7;
		}
		return (ByteBuffer)buffer.put((byte) length).put(bytes).flip();
	}

	/**
	 * read element from segment file
	 * 
	 * @return
	 */
	public E read() throws IOException {

		final byte[] bs;
		final Lock lock = this.lock;
		lock.lock();
		try {
			if (isClose.get())
				throw new IllegalStateException("Segment file closed");

			if (!isReadTail())
				bs = read0();
			else
				bs = null;
		} finally {
			lock.unlock();
		}
		return bs == null ? null : handler.deserialize(bs);
	}

	/**
	 * read element from segment file
	 * 
	 * @param timeout
	 *            (timeout <= 0, then blocking until can read)
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	public E read(long timeout, TimeUnit unit) throws InterruptedException, IOException {

		final byte[] bs;
		final Lock lock = this.lock;
		lock.lock();
		try {

			if (isClose.get())
				throw new IllegalStateException("Segment file closed");

			if (isReadTail()) {

				if (!isWriteFinish.get()) {
					if (timeout > 0) {
						readable.await(timeout, unit);
					} else {
						readable.await();
					}
					if (isReadTail())
						return null;
				} else {
					return null;
				}
			}
			bs = read0();
		} finally {
			lock.unlock();
		}
		return bs == null ? null : handler.deserialize(bs);
	}

	/**
	 * read data
	 * 
	 * @return
	 */
	private byte[] read0() {

		byte b = readBuffer.get();
		int length = b & 0x7F;
		for (int shift = 7; (b & 0x80) != 0; shift += 7) {
			b = readBuffer.get();
			length |= (b & 0x7F) << shift;
		}
		if (length <= 0)
			return null;
		byte[] bs = new byte[length];
		readBuffer.get(bs);
		flushReadPosition(readBuffer.position());
		return bs;
	}

	/**
	 * closeAndDelete this segment file this method will call close method and then
	 * closeAndDelete file
	 * 
	 * @return
	 */
	public boolean closeAndDelete() {

		IOUtils.closeQuietly(this);
		return segmentFile.delete();
	}

	/**
	 * close segment file handle
	 */
	public void close() {

		if (isClose.get())
			return;

		final Lock lock = this.lock;
		lock.lock();
		try {
			if (isClose.get())
				return;

			isClose.set(true);
			closeDirectBuffer(writeBuffer);
			IOUtils.closeQuietly(segmentFileChannel);
			IOUtils.closeQuietly(segmentFileAccessor);
			readable.signalAll();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * close MappedByteBuffer
	 * 
	 * @param cb
	 */
	private void closeDirectBuffer(ByteBuffer cb) {

		if (cb == null || !cb.isDirect())
			return;
		try {
			Method cleaner = cb.getClass().getMethod("cleaner");
			cleaner.setAccessible(true);
			Method clean = Class.forName("sun.misc.Cleaner").getMethod("clean");
			clean.setAccessible(true);
			clean.invoke(cleaner.invoke(cb));
		} catch (Throwable ignore) {
		}
	}
}
