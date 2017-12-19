package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.RequestMessage;
import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.Worker;

public class MobileServiceDemoWorker2 {
  MobileServiceDemo mobileservicedemo;
  Worker worker;

  public MobileServiceDemoWorker2() {
    mobileservicedemo = new MobileServiceDemo();
    worker = new Worker() {
	  @Override
	  public byte[] resolveRequest(byte[] packageBytes) {
		byte[] respBytes = null;
		  
		  // get request message
	    RequestMessage reqMsg = (RequestMessage) NetUtils.deserialize(packageBytes);
	    
	    switch (reqMsg.functionName) {
	    case "getFileList2": {
	      // for variable "path"
	      java.lang.String[] paths = new java.lang.String[reqMsg.inParams[0].values.length];
	      for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
	        paths[i] = (java.lang.String) reqMsg.inParams[0].values[i];
	      }
	      java.lang.String path = paths[0];
	
	      // start calling function "getFileList1"
	      java.lang.String[] rets = mobileservicedemo.getFileList2(path);
	      String retType = "java.lang.String[]";
	      ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, rets);
	
	      // convert to binary array and send it back to the broker
	      respBytes = NetUtils.serialize(respMsg);
	      break;
	    }
	    }
	    
	    return respBytes;
	  }
		
	  @Override
	  public String info() {
		String json = 
		  "{" +
			"\"code\" : \"REGISTER\"," +
			"\"id\" : \"" + worker.workerId + "\"," +
			"\"functions\" : [" +
			  "{" +
			    "\"functionName\" : \"getFileList2\"," +
				"\"inParams\" : [\"java.lang.String\"]," +
				"\"outParam\" : \"java.lang.String[]\"" + 
			  "}" + 
			"]" +
		  "}";
		return json;
	  }
	  
	};
  }

}
