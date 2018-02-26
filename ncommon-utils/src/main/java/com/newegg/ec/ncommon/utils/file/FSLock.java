package com.newegg.ec.ncommon.utils.file;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.atomic.AtomicBoolean;

import com.newegg.ec.ncommon.utils.io.IOUtils;

/**
 * 
 * @author lz31
 *
 */
public final class FSLock implements Closeable {
	
	private final RandomAccessFile raf ;
	private final FileChannel fc;
	private volatile FileLock lock;
	private final AtomicBoolean closed = new AtomicBoolean(false);
	
	public FSLock(File lockFile) throws IOException {
		
		if(lockFile == null)
			throw new NullPointerException("lock file is null");
		
		File dir = lockFile.getCanonicalFile().getParentFile();
		FileUtils.createDirIfNeed(dir);
		this.raf =  new RandomAccessFile(lockFile, "rw" );
		this.fc = raf.getChannel();
	}
	
	/**
	 * blocking until get lock;
	 * @blocking
	 * @throws IOException
	 */
	public synchronized void lock() throws IOException {
		
		if(closed.get())
			throw new IOException("FS Lock closed");
		
		if(lock != null)
			return;
			
		lock = fc.lock();
	}
	
	/**
	 * return true if get lock else return false, not blocking
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean tryLock() throws IOException {
		
		if(closed.get())
			throw new IOException("FS Lock closed");
		
		if(lock != null)
			return true;
		
		lock = fc.tryLock();
		return lock != null;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public synchronized void release() throws IOException {
		
		if(closed.get())
			throw new IOException("FS Lock closed");
		
		if(lock == null) 
			return;
		
		try {
			lock.release();
		} finally {
			lock = null;
		}
	}
	
	@Override
	public synchronized void close() throws IOException {
		
		if(closed.get())
			return;
		
		try {
			IOUtils.closeQuietly(fc);
			IOUtils.closeQuietly(raf);
		} finally {
			closed.set(true);
		}
	}
}
