package org.zicat.common.utils.io;

import java.io.Closeable;
import java.util.Collection;

/**
 *
 */
public class IOUtils {

	/**
	 * close quiet
	 * 
	 * @param closeable
	 */
	public static void closeQuietly(Closeable closeable) {

		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (Throwable ioe) {
		}
	}

	/**
	 * close quiet
	 * 
	 * @param closeables
	 */
	public static void closeQuietly(Closeable... closeables) {

		if (closeables != null) {
			for (Closeable closeable : closeables) {
				closeQuietly(closeable);
			}
		}
	}

	/**
	 * close quiet
	 * 
	 * @param closeables
	 */
	public static void closeQuietly(Collection<Closeable> closeables) {

		if (closeables != null) {
			for (Closeable closeable : closeables) {
				closeQuietly(closeable);
			}
		}
	}
}
