package com.usu.tinyservice.classes;

import com.usu.tinyservice.messages.RequestMessage;
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
			
			switch (reqMsg.funcName) {
				case "getRoot": {
					String ret = mobileServiceDemo.getRoot();
					send(ret);
					break;
				}
				case "getFileList": {
					
				}
			}
		}
	}
}
