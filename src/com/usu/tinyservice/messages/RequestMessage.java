package com.usu.tinyservice.messages;

public class RequestMessage extends Message {
	public InParam[] inParams = new InParam[0];
	
	public RequestMessage() { 
		super();
	}
	
	public RequestMessage(String functionName) {
		super(functionName);
	}
	
	public RequestMessage(String functionName, String outType, String[] values) {
		super(functionName, outType, values);
	}

}
