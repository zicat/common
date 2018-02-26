package com.newegg.ec.ncommon.utils.test.file;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author lz31
 *
 */
@XmlRootElement(name = "project")
public class AAConfig {
	
	private DevelopersConfig developersConfig;
	
	@XmlElement(name = "developers")
	public DevelopersConfig getDevelopersConfig() {
		return developersConfig;
	}

	public void setDevelopersConfig(DevelopersConfig developersConfig) {
		this.developersConfig = developersConfig;
	}
}
