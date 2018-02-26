package org.zicat.common.utils.test.file;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author lz31
 *
 */
public class DevelopersConfig {
	
	private List<DeveloperConfig> developerConfig;
	
	@XmlElement(name = "developer")
	public List<DeveloperConfig> getDeveloperConfig() {
		return developerConfig;
	}

	public void setDeveloperConfig(List<DeveloperConfig> developerConfig) {
		this.developerConfig = developerConfig;
	}
}
