package com.usu.tinyservice.messages.json;

/**
 * define the output parameter containing only value 
 * 
 * @author minhld
 *
 */
public class JsonOutParam {
	public String type;
	public String[] values = new String[0];
	
	
	public JsonOutParam() {
		this("void");
	}
	
	public JsonOutParam(String type) {
		this.type = type;
	} 
	
	public JsonOutParam(String type, String[] values) {
		this.type = type;
		this.values = values;
	}
}
