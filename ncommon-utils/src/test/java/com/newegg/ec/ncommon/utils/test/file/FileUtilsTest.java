package com.newegg.ec.ncommon.utils.test.file;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.newegg.ec.ncommon.utils.file.FSLock;
import com.newegg.ec.ncommon.utils.file.FileUtils;

import junit.framework.Assert;

/**
 * 
 * @author lz31
 *
 */
public class FileUtilsTest {
	
	public static final String fileName = "./aaa/bbb/lyntest.txt";
	
	@Test
	public void testCreateNewFile() throws IOException {
		
		try {
			FileUtils.createNewFile((String)null, false);
			Assert.assertTrue(false);
		} catch(NullPointerException e) {
			Assert.assertTrue(true);
		}
		
		File file = new File(fileName);
		FileUtils.createNewFile(fileName, true);
		Assert.assertTrue(file.exists());
		FileUtils.createNewFile(fileName, true);
		Assert.assertTrue(file.exists());
		FileUtils.createNewFile(fileName, true);
		Assert.assertTrue(file.exists());
		FileUtils.createNewFile(fileName, false);
		Assert.assertTrue(file.exists());
		
		FSLock lock = new FSLock(file);
		try {
			lock.lock();
			FileUtils.createNewFile(fileName, true);
		} catch(IOException e) {
			Assert.assertTrue(true);
		} finally {
			try {
				lock.release();
			} finally {
				lock.close();
			}
		}
		
		Assert.assertTrue(file.exists());
		Assert.assertTrue(file.delete());
		FileUtils.createNewFile(fileName, false);
		Assert.assertTrue(file.exists());
		FileUtils.cleanUpDir(file.getParentFile().getParent());
		Assert.assertTrue(file.getParentFile().getParentFile().delete());
	}
}
