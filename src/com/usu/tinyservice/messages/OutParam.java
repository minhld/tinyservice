package com.usu.tinyservice.messages;

/**
 * define the output parameter containing only value 
 * 
 * @author minhld
 *
 */
public class OutParam {
	public String[] values = new String[0];
	public String type;
	
	public OutParam() {
		this("void");
	}
	
	public OutParam(String type) {
		this.type = type;
	} 
	
	public OutParam(String[] values, String type) {
		this.values = values;
		this.type = type;
	}
}
