package com.newegg.ec.ncommon.utils.test.file;

import javax.xml.bind.annotation.XmlValue;

/**
 * 
 * @author lz31
 *
 */
public class NameConfig {
	
	private String value;
	
	@XmlValue
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
