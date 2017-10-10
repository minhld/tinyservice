package com.usu.tinyservice.messages.binary;

import com.usu.tinyservice.messages.json.JsonMessage;

public class ResponseMessage extends JsonMessage {
	private static final long serialVersionUID = 1L;

	public ResponseMessage() { 
		super();
	}
	
	public ResponseMessage(String functionName) {
		super(functionName);
	}
	
	public ResponseMessage(String functionName, String outType, String[] values) {
		super(functionName, outType, values);
	}
	
	public ResponseMessage(String messageId, String functionName, String outType, String[] values) {
		super(functionName, outType, values);
		this.messageId = messageId;
	}
}
