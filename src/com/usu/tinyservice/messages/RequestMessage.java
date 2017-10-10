package com.usu.tinyservice.messages;

public class RequestMessage extends Message {
	private static final long serialVersionUID = 1L;
	
	public InJsonParam[] inParams = new InJsonParam[0];
	
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
