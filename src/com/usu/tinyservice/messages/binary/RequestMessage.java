package com.usu.tinyservice.messages.binary;

public class RequestMessage extends Message {
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("rawtypes")
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
