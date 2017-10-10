package com.usu.tinyservice.messages;

/**
 * define the output parameter containing only value 
 * 
 * @author minhld
 *
 */
public class OutJsonParam {
	public String type;
	public String[] values = new String[0];
	
	
	public OutJsonParam() {
		this("void");
	}
	
	public OutJsonParam(String type) {
		this.type = type;
	} 
	
	public OutJsonParam(String type, String[] values) {
		this.type = type;
		this.values = values;
	}
}
