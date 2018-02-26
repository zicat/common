package com.newegg.ec.ncommon.utils.test.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

import com.newegg.ec.ncommon.utils.file.FSLock;
import com.newegg.ec.ncommon.utils.io.IOUtils;

/**
 * 
 * @author lz31
 *
 */
public class FSLockTest {
	
	@Test
	public void test() throws IOException {
		
		File file = new File("aa.lock");
		FSLock lock = null;
		try {
			lock = new FSLock(file);
			try {
				lock.lock();
				lock.lock();
				Assert.assertTrue(lock.tryLock());
			} finally {
				lock.release();
			}
			
			try {
				Assert.assertTrue(lock.tryLock());
				Assert.assertTrue(lock.tryLock());
			}  finally {
				lock.release();
			}
		} finally {
			IOUtils.closeQuietly(lock);
		}
		
		Files.delete(file.toPath());
	}
}
