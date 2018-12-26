package com.usu.tinyservice.messages.binary;

public class RequestMessage extends Message {
	private static final long serialVersionUID = 1L;

	public enum RequestType {
		ORIGINAL,
		FORWARDING
	}

	public RequestType requestType = RequestType.ORIGINAL;

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

	public RequestMessage cloneMessage() {
		try {
			return (RequestMessage) clone();
		} catch (Exception e) {
			return null;
		}
	}
}
