package com.usu.tinyservice.messages.binary;

import com.usu.tinyservice.messages.json.JsonInParam;
import com.usu.tinyservice.messages.json.JsonMessage;

public class RequestMessage extends JsonMessage {
	private static final long serialVersionUID = 1L;
	
	public JsonInParam[] inParams = new JsonInParam[0];
	
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
