package com.usu.tinyservice.messages.json;

public class JsonResponseMessage extends JsonMessage {
	private static final long serialVersionUID = 1L;

	public JsonResponseMessage() { 
		super();
	}
	
	public JsonResponseMessage(String functionName) {
		super(functionName);
	}
	
	public JsonResponseMessage(String functionName, String outType, String[] values) {
		super(functionName, outType, values);
	}
	
	public JsonResponseMessage(String messageId, String functionName, String outType, String[] values) {
		super(functionName, outType, values);
		this.messageId = messageId;
	}
}
