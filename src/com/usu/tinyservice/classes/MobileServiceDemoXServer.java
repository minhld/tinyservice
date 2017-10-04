package com.usu.tinyservice.classes;

import com.usu.tinyservice.messages.RequestMessage;
import com.usu.tinyservice.messages.ResponseMessage;
import com.usu.tinyservice.network.JSONHelper;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.Responder;

/**
 * this is a demo server instance of the MobileServiceDemo
 * 
 * @author minhld
 *
 */
public class MobileServiceDemoXServer {
	MobileServiceDemo mobileServiceDemo;
	ResponderX resp;
	
	public MobileServiceDemoXServer() {
		// core function
		this.mobileServiceDemo = new MobileServiceDemo();

		// start the Responder
		this.resp = new ResponderX();
		this.resp.start();
	}
	
	class ResponderX extends Responder {
		@Override
		public void respond(byte[] req) {
			String reqJSON = new String(req);
			RequestMessage reqMsg = JSONHelper.getRequest(reqJSON);
			
			switch (reqMsg.functionName) {
				case "getRoot": {
					// create input parameters
					
					// execute the function
					String ret = mobileServiceDemo.getRoot();
					
					// prepare the output parameters
					String retType = "String";
					String[] retValues = NetUtils.getString(ret);
					ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, retValues);
					String respJSON = JSONHelper.createResponse(respMsg);
					send(respJSON);
					
					break;
				}
				case "getFileList": {
					// create input parameters
					String path = (String) reqMsg.inParams[0].values[0];
					boolean fileOnly = Boolean.getBoolean(reqMsg.inParams[1].values[0]);
					
					// execute the function
					String[] rets = mobileServiceDemo.getFileList(path, fileOnly);
					
					// prepare the output parameters
					String retType = "String";
					String[] retValues = NetUtils.getStringArray(rets);
					ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, retValues);
					String respJSON = JSONHelper.createResponse(respMsg);
					send(respJSON);
					
					break;
				}
			}
		}
	}
}
