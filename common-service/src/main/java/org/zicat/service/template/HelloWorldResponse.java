package org.zicat.service.template;

/**
 * @ This is a demo
 * @author lz31
 */
public class HelloWorldResponse {
	
	private String name;
	private int code;
	
	public HelloWorldResponse() {
		
	}
	
	public HelloWorldResponse(String name, int code) {
		this.name = name;
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
}
