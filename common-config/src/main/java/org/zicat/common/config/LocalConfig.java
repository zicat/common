package org.zicat.common.config;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.zicat.common.config.schema.InputStreamSchema;
import org.zicat.common.utils.io.IOUtils;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public class LocalConfig<T> extends AbstractConfig<URL, T> {
	
	protected final Path dirPath;
	protected final String name;
	protected final File file;
	protected final InputStreamSchema<T> schema;
	protected volatile long lasteditTime = -1;
	
	public LocalConfig(URL path, InputStreamSchema<T> schema, AbstractConfig<?, T> parentConfig) {
		
		super(parentConfig, path);
		
		if(schema == null)
			throw new NullPointerException("schema is null");
		
		File file = new File(path.getFile());
		if(!file.exists() || file.isDirectory())
			throw new IllegalStateException(path + " is not a file");
		
		this.file = file;
		this.name = file.getName();
		this.dirPath = Paths.get(file.getParentFile().toURI());
		this.schema = schema;
	}
	
	public LocalConfig(String path, InputStreamSchema<T> schema, LocalConfig<T> parent) {
		this(getPath(path), schema, parent);
	}
	
	public LocalConfig(URL path, InputStreamSchema<T> schema) {
		this(path, schema, null);
	}
	
	public LocalConfig(String path, InputStreamSchema<T> schema) {
		this(getPath(path), schema, null);
	}
	
	/**
	 * find class path first, find current directory second
	 * @param path
	 * @return
	 */
	private static URL getPath(String path) {
		
		if(path == null)
			return null;
		
		URL url = Thread.currentThread().getContextClassLoader().getResource(path);
		
		if(url == null) {
			
			File file = new File(path);
			if(!file.exists() || !file.isFile())
				throw new IllegalStateException(path + " illegal");
			
			try {
				url = file.toURI().toURL();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return url;
	}
	
	@Override
	protected T newInstance(T parentInstance) throws Exception {
		
		InputStream stream = null;
		try {
			stream = source.openStream();
			return createInstanceBySchema(schema, stream);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public final Path getDirPath() {
		return dirPath;
	}

	/**
	 * 
	 * @return
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return
	 */
	public final File getFile() {
		return file;
	}
	
	@Override
	public boolean isModify() {
		
		long currentModify = file.lastModified();
		if(currentModify != lasteditTime) {
			lasteditTime = currentModify;
			return true;
		}
		return false;
	}
}
