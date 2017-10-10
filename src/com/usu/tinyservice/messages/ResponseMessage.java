package com.usu.tinyservice.messages;

public class ResponseMessage extends Message {
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
