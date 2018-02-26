package org.zicat.common.utils.test.file;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author lz31
 *
 */
public class DeveloperConfig {
	
	private NameConfig nameConfig;
	
	@XmlElement(name = "name")
	public NameConfig getNameConfig() {
		return nameConfig;
	}

	public void setNameConfig(NameConfig nameConfig) {
		this.nameConfig = nameConfig;
	}
}
