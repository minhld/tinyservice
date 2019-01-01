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

	@Override
	public RequestMessage clone() {
		RequestMessage reqMsg = new RequestMessage(functionName);
		reqMsg.sessionId = this.sessionId;
		reqMsg.requestType = this.requestType;
		reqMsg.messageId = this.messageId;
		reqMsg.inParams = new InParam[this.inParams.length];
		for (int i = 0; i < this.inParams.length; i++) {
			reqMsg.inParams[i] = new InParam(this.inParams[i].param, 
					this.inParams[i].type, this.inParams[i].values.clone());
		}
		reqMsg.outParam = this.outParam;
		return reqMsg;
	}
}
