package com.usu.tinyservice.classes;

import com.usu.tinyservice.messages.InParam;
import com.usu.tinyservice.messages.RequestMessage;
import com.usu.tinyservice.network.JSONHelper;
import com.usu.tinyservice.network.ReceiveListener;
import com.usu.tinyservice.network.Requester;

/**
 * this is a demo client instance of the MobileServiceDemo 
 * 
 * @author minhld
 *
 */
public class MobileServiceDemoXClient {
	private RequesterX req;
	public ReceiveListener listener;
	
	public MobileServiceDemoXClient(ReceiveListener listener) {
		// start listener
		this.listener = listener;
		
		// start the Requester
		this.req = new RequesterX();
		this.req.start();
	}
	
	public void getRoot() {
		// compose input parameters
		String functionName = "getRoot";
		String outType = "String";
		RequestMessage reqMsg = new RequestMessage(functionName, outType);
		
		// create request message and send
		String msgJSON = JSONHelper.createRequest(reqMsg);
		req.send(msgJSON);
	}
	
	public void getFileList(String path, boolean fileOnly) {
		// compose input parameters
		String functionName = "getFileList";
		String outType = "String[]";
		RequestMessage reqMsg = new RequestMessage(functionName, outType);
		reqMsg.inParams = new InParam[2];
		reqMsg.inParams[0] = new InParam("path", "String", new String[] { "D:\\" });
		reqMsg.inParams[1] = new InParam("fileOnly", "boolean", new String[] { "true" });
		
		
		

		// create request message and send
		String msgJSON = JSONHelper.createRequest(reqMsg);
		req.send(msgJSON);
	}
	
	class RequesterX extends Requester {
		@Override
		public void receive(byte[] resp) {
			listener.dataReceived(resp);
		}
	}

}
