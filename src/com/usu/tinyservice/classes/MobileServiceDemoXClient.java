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
		this.req = new RequesterX();
		this.listener = listener;
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
		InParam[] inParams = new InParam[2];
		inParams[0] = new InParam("path", "String", new String[] { "D:\\" });
		inParams[1] = new InParam("fileOnly", "boolean", new String[] { "true" });
		String outType = "String[]";
		RequestMessage reqMsg = new RequestMessage(functionName, outType);

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
