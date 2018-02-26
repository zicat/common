package org.zicat.common.config.schema;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Properties;

import com.google.gson.Gson;
import org.zicat.common.config.schema.json.GsonSchema;
import org.zicat.common.config.schema.properties.PropertiesSchema;
import org.zicat.common.config.schema.xml.JAXBSchema;

/**
 * 
 * @author lz31
 *
 */
public class InputStreamSchemaFactory {
	
	/**
	 * 
	 * @param clazz
	 * @param charset
	 * @return
	 */
	public static <T> InputStreamSchema<T> createJAXBSchema(Class<T> clazz, Charset charset) {
		return new JAXBSchema<>(clazz, charset);
	}
	
	/**
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> InputStreamSchema<T> createJAXBSchema(Class<T> clazz) {
		return new JAXBSchema<>(clazz, null);
	}
	
	/**
	 * 
	 * @param charset
	 * @return
	 */
	public static InputStreamSchema<Properties> createPropertiesSchema(Charset charset) {
		return new PropertiesSchema(charset);
	}
	
	/**
	 * 
	 * @return
	 */
	public static InputStreamSchema<Properties> createPropertiesSchema() {
		return new PropertiesSchema(null);
	}
	
	/**
	 * 
	 * @param type
	 * @param gson
	 * @param charset
	 * @return
	 */
	public static <T> InputStreamSchema<T> createGsonSchema(Type type, Gson gson, Charset charset) {
		return new GsonSchema<>(type, gson, charset);
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static <T> InputStreamSchema<T> createGsonSchema(Type type) {
		return new GsonSchema<>(type, null, null);
	}
}
