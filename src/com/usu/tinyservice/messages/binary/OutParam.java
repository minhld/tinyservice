package com.usu.tinyservice.messages.binary;

import java.io.Serializable;

/**
 * define the output parameter containing only value 
 * 
 * @author minhld
 *
 */
public class OutParam implements Serializable {
	private static final long serialVersionUID = 1L;
	
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
