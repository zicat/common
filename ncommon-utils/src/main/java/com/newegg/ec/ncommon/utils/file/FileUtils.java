package com.newegg.ec.ncommon.utils.file;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author lz31
 *
 */
public class FileUtils {
	
	/**
	 * 
	 * @param fileName
	 * @param removeOld
	 * @throws IOException
	 */
	public static void createNewFile(String fileName, boolean removeOld) throws IOException {
		
		File file = fileName == null?null: new File(fileName);
		createNewFile(file, removeOld);
	}
	
	/**
	 * 
	 * @param file
	 * @param removeOld
	 * @throws IOException
	 */
	public static void createNewFile(File file, boolean removeOld) throws IOException {
		
		if(file == null)
			throw new NullPointerException("file is null");
		
		file = file.getCanonicalFile();
		
		createDirIfNeed(file.getParentFile());
		if(file.exists() && removeOld && !file.delete())	
			throw new IOException("delete file " + file.getName() + " failed");
		
		if(!file.exists() && !file.createNewFile())
			throw new IOException("create file " + file.getName() + " failed");
	}

	/**
	 * create dir if need(recursive)
	 * @param dir
	 * @throws IOException 
     */
	public static void createDirIfNeed(File dir) throws IOException {
		
		if(dir == null)
			throw new NullPointerException("dir is null");
		
		if(dir.exists()) {
			
			if(!dir.isDirectory())
				throw new RuntimeException(dir.getPath() + " is exist and is not a dir");
			else
				return;
		}
		
		File parentDir = dir.getCanonicalFile().getParentFile();
		if(!parentDir.exists()) {
			createDirIfNeed(parentDir);
		}
		
		dir.mkdir();
	}

	/**
	 * create dir if need(recursive)
	 * @param dir
	 * @throws IOException 
     */
	public static void createDirIfNeed(String dir) throws IOException {
		
		if(dir == null) 
			throw new NullPointerException("dir is null");
		
		createDirIfNeed(new File(dir));
	}

	/**
	 * clean up dir(recursive)
	 * @param dir
     */
	public static void cleanUpDir(File dir) {
		
		if(dir == null || !dir.isDirectory())
			return;
		
		File[] files = dir.listFiles();
		
		for(File f: files) {
			
			if(f.isDirectory())
				cleanUpDir(f);
			f.delete();
		}
	}

	/**
	 * clean up dir(recursive)
	 * @param dir
     */
	public static void cleanUpDir(String dir) {
		
		if(dir == null)
			return;
		cleanUpDir(new File(dir));
	}
}
