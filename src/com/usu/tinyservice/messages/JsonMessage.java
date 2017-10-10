package com.usu.tinyservice.messages;

import java.io.Serializable;
import java.util.UUID;


public class JsonMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String messageId;
	public String functionName;
	public JsonOutParam outParam;
	
	public JsonMessage() { 
		this.messageId = UUID.randomUUID().toString();
	}

	public JsonMessage(String functionName) {
		this();
		this.functionName = functionName;
	}
	
	public JsonMessage(String functionName, String outType, String[] values) {
		this(functionName);
		this.outParam = new JsonOutParam(outType, values);
	}

}
