package com.usu.tinyservice.messages.binary;

/**
 * define the input parameter class to hold a pair of key-value
 * @author minhld
 *
 */
public class InParam {
	public String param;
	public String type;
	public String[] values;
	
	public InParam() { } 
	
	public InParam(String param, String type, String[] values) {
		this.param = param;
		this.type = type;
		this.values = values;
	}
}
