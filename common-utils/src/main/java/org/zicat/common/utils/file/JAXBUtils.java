package org.zicat.common.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.zicat.common.utils.io.IOUtils;

/**
 * 
 * @author lz31
 *
 */
public class JAXBUtils {
	
	/**
	 * 
	 * @param xmlFileName
	 * @param clazz
	 * @return
	 * @throws IOException 
	 * @throws JAXBException 
	 */
	public static <T> T unmarshal(String xmlFileName, Class<T> clazz) throws IOException, JAXBException {
		return unmarshal(xmlFileName, clazz, StandardCharsets.UTF_8);
	}
	
	/**
	 * 
	 * @param xmlFileName
	 * @param clazz
	 * @param charset
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static <T> T unmarshal(String xmlFileName, Class<T> clazz, Charset charset) throws IOException, JAXBException {
		
		if(xmlFileName == null)
			throw new NullPointerException("config name is null");
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFileName);
		if(is == null) {
			is = new FileInputStream(new File(xmlFileName).getCanonicalFile());
		}
		try {
			return unmarshal(is, clazz, charset);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
	
	/**
	 * 
	 * @param is
	 * @param clazz
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static <T> T unmarshal(InputStream is, Class<T> clazz) throws IOException, JAXBException {
		return unmarshal(is, clazz, StandardCharsets.UTF_8);
	}
	
	/**
	 * 
	 * @param is
	 * @param clazz
	 * @param charset
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static <T> T unmarshal(InputStream is, Class<T> clazz, Charset charset) throws IOException, JAXBException {
		
		if(is == null)
			throw new NullPointerException("InputStream is null");
		
		Reader reader = new InputStreamReader(is, charset);
		return unmarshal(reader, clazz);
	}
	
	/**
	 * 
	 * @param reader
	 * @param clazz
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(Reader reader, Class<T> clazz) throws IOException, JAXBException {
		
		if(reader == null)
			throw new NullPointerException("reader is null");
		
		if(clazz == null)
			throw new NullPointerException("class is null");
		
		JAXBContext context = JAXBContext.newInstance(clazz);
		Unmarshaller um = context.createUnmarshaller();
		return (T) um.unmarshal(reader);
	}
	
}
