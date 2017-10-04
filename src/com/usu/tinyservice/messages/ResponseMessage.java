package com.usu.tinyservice.messages;

public class ResponseMessage extends Message {
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
