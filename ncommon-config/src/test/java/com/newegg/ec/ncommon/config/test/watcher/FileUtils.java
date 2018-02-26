package com.newegg.ec.ncommon.config.test.watcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
	
	/**
	 * 
	 * @param path
	 * @param data
	 * @throws IOException
	 */
	public static void createNewFile(File file, byte[] data) throws IOException {
		
		if(!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(file);
			os.write(data);
			os.flush();
		} finally {
			os.close();
		}
	}
}
