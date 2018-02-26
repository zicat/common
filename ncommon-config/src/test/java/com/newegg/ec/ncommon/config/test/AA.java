package com.newegg.ec.ncommon.config.test;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author lz31
 *
 */
@XmlRootElement(name = "aa")
public class AA {
	
	private String name;
	
	public AA() {
		super();
	}

	public AA(String name) {
		super();
		this.name = name;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(!(other instanceof AA))
			return false;
		
		AA aa = (AA) other;
		if(getName() == aa.getName())
			return true;
		
		if(getName() == null || (!getName().equals(aa.getName())))
			return false;
		
		return true;
	}
}
