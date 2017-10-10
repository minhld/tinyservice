package com.usu.tinyservice.messages;

public class JsonRequestMessage extends JsonMessage {
	private static final long serialVersionUID = 1L;
	
	public JsonInParam[] inParams = new JsonInParam[0];
	
	public JsonRequestMessage() { 
		super();
	}
	
	public JsonRequestMessage(String functionName) {
		super(functionName);
	}
	
	public JsonRequestMessage(String functionName, String outType) {
		super(functionName, outType, new String[0]);
	}

}
