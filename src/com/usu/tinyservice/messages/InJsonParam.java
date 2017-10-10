package com.usu.tinyservice.messages;

/**
 * define the input parameter class to hold a pair of key-value
 * @author minhld
 *
 */
public class InJsonParam {
	public String param;
	public String type;
	public String[] values;
	
	public InJsonParam() { } 
	
	public InJsonParam(String param, String type, String[] values) {
		this.param = param;
		this.type = type;
		this.values = values;
	}
}
