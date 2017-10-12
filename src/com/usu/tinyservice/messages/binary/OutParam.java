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
	public Object[] values = new Object[0];
	
	
	public OutParam() {
		this("void");
	}
	
	public OutParam(String type) {
		this.type = type;
	} 
	
	public OutParam(String type, Object[] values) {
		this.type = type;
		this.values = values;
	}
}
