package com.usu.tinyservice.messages;

import java.util.UUID;


public class Message {
	public String messageId;
	public String functionName;
	public OutParam outParam;
	
	public Message() { 
		this.messageId = UUID.randomUUID().toString();
	}

	public Message(String functionName) {
		this();
		this.functionName = functionName;
	}
	
	public Message(String functionName, String outType, String[] values) {
		this(functionName);
		this.outParam = new OutParam(outType, values);
	}

}
