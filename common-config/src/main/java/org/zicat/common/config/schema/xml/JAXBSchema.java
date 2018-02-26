package org.zicat.common.config.schema.xml;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.zicat.common.config.schema.InputStreamSchema;
import org.zicat.common.utils.file.JAXBUtils;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public class JAXBSchema<T> implements InputStreamSchema<T> {
	
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	
	private final Class<T> clazz;
	private final Charset charset;
	
	public JAXBSchema(Class<T> clazz, Charset charset) {
		
		if(clazz == null)
			throw new NullPointerException("class is null");
		
		this.clazz = clazz;
		this.charset = charset == null?DEFAULT_CHARSET: charset;
	}

	@Override
	public T unmarshal(InputStream source) throws Exception {
		return JAXBUtils.unmarshal(source, clazz, charset);
	}

	@Override
	public T unmarshal(T parentTarget, InputStream source) throws Exception {
		throw new UnsupportedOperationException("JAXBSchema not support unmarshal(T parentTarget, InputStream source)");
	}
}
