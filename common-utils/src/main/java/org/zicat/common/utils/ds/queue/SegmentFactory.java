package org.zicat.common.utils.ds.queue;

import org.zicat.common.utils.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author lz31
 * @ThreadSafe
 * @param <E>
 */
public class SegmentFactory<E> {

	final File directory;
	final File idFile;
	final int length;
	final SerializableHandler<E> handler;
	final boolean cleanUpOnStart;

	/**
	 *
	 * @param idFile
	 * @param length
	 * @param handler
	 * @throws IOException
	 */
	public SegmentFactory(File idFile, int length, SerializableHandler<E> handler) throws IOException {

		this(idFile, length, handler, false);
	}

	/**
	 *
	 * @param idFile
	 * @param length
	 * @param handler
	 * @param cleanUpOnStart
	 * @throws IOException
	 */
	public SegmentFactory(File idFile, int length, SerializableHandler<E> handler, boolean cleanUpOnStart) throws IOException {

		if (idFile == null)
			throw new NullPointerException("directory is null");

		if (handler == null)
			throw new NullPointerException("serializable handler is null");

		this.idFile = idFile;
		this.directory = idFile.getParentFile();
		this.length = length;
		this.handler = handler;
		this.cleanUpOnStart = cleanUpOnStart;
		FileUtils.createDirIfNeed(directory);
	}

	/**
	 * create new segment
	 * 
	 * @param segmentFile
	 * @return
	 * @throws IOException
	 */
	public Segment<E> createNewSegment(File segmentFile) throws IOException {

		return new Segment<>(segmentFile, length, handler);
	}

	/**
	 * create new segment
	 * 
	 * @return
	 * @throws IOException
	 */
	public Segment<E> createNewSegment() throws IOException {

		return createNewSegment(getUniqueFile(directory, ".seg"));
	}

	/**
	 * get unique file
	 * 
	 * @param directory
	 * @param suff
	 * @return
	 */
	private File getUniqueFile(File directory, String suff) {

		File file;
		do {
			file = new File(directory, idFile.getName() + "." + UUID.randomUUID().toString() + suff);
		} while (file.exists());

		return file;
	}
}
