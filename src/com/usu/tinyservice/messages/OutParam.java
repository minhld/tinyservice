package com.usu.tinyservice.messages;

/**
 * define the output parameter containing only value 
 * 
 * @author minhld
 *
 */
public class OutParam {
	public String type;
	public String[] values = new String[0];
	
	
	public OutParam() {
		this("void");
	}
	
	public OutParam(String type) {
		this.type = type;
	} 
	
	public OutParam(String type, String[] values) {
		this.type = type;
		this.values = values;
	}
}
