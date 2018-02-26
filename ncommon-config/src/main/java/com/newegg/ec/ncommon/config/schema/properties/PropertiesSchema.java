package com.newegg.ec.ncommon.config.schema.properties;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Properties;

import com.newegg.ec.ncommon.config.schema.InputStreamSchema;

/**
 * 
 * @author lz31
 *
 */
public class PropertiesSchema implements InputStreamSchema<Properties> {
	
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	
	private final Charset charset;
	
	public PropertiesSchema(Charset charset) {
		this.charset = charset == null?DEFAULT_CHARSET: charset;
	}

	@Override
	public Properties unmarshal(InputStream source) throws Exception {
		Properties prop = new Properties();
		prop.load(new InputStreamReader(source, charset));
		return prop;
	}

	@Override
	public Properties unmarshal(Properties parentProperties, InputStream source) throws Exception {
		
		Properties properties = unmarshal(source);
		
		if(parentProperties == null)
			return properties;
		
		Enumeration<Object> parentKeys = parentProperties.keys();
		while(parentKeys != null && parentKeys.hasMoreElements()) {
			Object key = parentKeys.nextElement();
			if(!properties.containsKey(key))
				properties.put(key, parentProperties.get(key));
		}
		return properties;
	}
}
