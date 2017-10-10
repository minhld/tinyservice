package com.usu.tinyservice.messages.json;

/**
 * define the input parameter class to hold a pair of key-value
 * @author minhld
 *
 */
public class JsonInParam {
	public String param;
	public String type;
	public String[] values;
	
	public JsonInParam() { } 
	
	public JsonInParam(String param, String type, String[] values) {
		this.param = param;
		this.type = type;
		this.values = values;
	}
}
