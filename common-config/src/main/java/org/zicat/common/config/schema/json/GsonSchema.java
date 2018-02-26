package org.zicat.common.config.schema.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import org.zicat.common.config.schema.InputStreamSchema;

/**
 * 
 * @author lz31
 *
 * @param <T>
 */
public class GsonSchema<T> implements InputStreamSchema<T> {

	private static final Gson DEFAULT_GSON = new Gson();
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	
	private final Type type;
	private final Gson gson;
	private Charset charset;
	
	public GsonSchema(Type type, Gson gson, Charset charset) {
		
		if(type == null)
			throw new NullPointerException("type is null");
		
		this.type = type;
		this.gson = gson == null?DEFAULT_GSON: gson;
		this.charset = charset == null?DEFAULT_CHARSET: charset;
	}
	
	@Override
	public T unmarshal(InputStream source) throws Exception {
		return gson.fromJson(new InputStreamReader(source, charset), type);
	}

	@Override
	public T unmarshal(T parentTarget, InputStream source) throws Exception {
		throw new UnsupportedOperationException("GsonSchema not support unmarshal(T parentTarget, InputStream source)");
	}
}
