package com.usu.tinyservice.classes;

import com.usu.tinyservice.messages.RequestMessage;
import com.usu.tinyservice.messages.ResponseMessage;
import com.usu.tinyservice.network.JSONHelper;
import com.usu.tinyservice.network.Responder;

/**
 * this is how a generated Mobile Service class looks like 
 * creates _Server class
 * @author minhld
 *
 */
public class MobileServiceDemoXServer {
	MobileServiceDemo mobileServiceDemo;
	ResponderX resp;
	
	public MobileServiceDemoXServer() {
		mobileServiceDemo = new MobileServiceDemo();
		resp = new ResponderX();
	}
	
	class ResponderX extends Responder {
		@Override
		public void respond(byte[] req) {
			String reqJSON = new String(req);
			RequestMessage reqMsg = JSONHelper.getRequest(reqJSON);
			
			switch (reqMsg.functionName) {
				case "getRoot": {
					String ret = mobileServiceDemo.getRoot();
					ResponseMessage respMsg = new ResponseMessage(reqMsg.functionName, "String", new String[] { ret });
					String respJSON = JSONHelper.createResponse(respMsg);
					send(respJSON);
					break;
				}
				case "getFileList": {
					
				}
			}
		}
	}
}
