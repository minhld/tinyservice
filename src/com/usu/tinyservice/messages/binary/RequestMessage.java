package com.usu.tinyservice.messages.binary;

import java.io.Serializable;

public class RequestMessage extends Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public InParam[] inParams = new InParam[0];
	
	public RequestMessage() { 
		super();
	}
	
	public RequestMessage(String functionName) {
		super(functionName);
	}
	
	public RequestMessage(String functionName, String outType) {
		super(functionName, outType, new String[0]);
	}

}
