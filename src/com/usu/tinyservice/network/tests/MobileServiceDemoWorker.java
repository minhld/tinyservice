package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.RequestMessage;
import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.Worker;

public class MobileServiceDemoWorker {
  MobileServiceDemo mobileservicedemo;
  Worker worker;

  public MobileServiceDemoWorker() {
    mobileservicedemo = new MobileServiceDemo();
    worker = new Worker() {
	  @Override
	  public byte[] resolveRequest(byte[] packageBytes) {
		byte[] respBytes = null;
		  
		  // get request message
	    RequestMessage reqMsg = (RequestMessage) NetUtils.deserialize(packageBytes);
	    
	    switch (reqMsg.functionName) {
	    case "getFileList1": {
	      // for variable "path"
	      java.lang.String[] paths = new java.lang.String[reqMsg.inParams[0].values.length];
	      for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
	        paths[i] = (java.lang.String) reqMsg.inParams[0].values[i];
	      }
	      java.lang.String path = paths[0];
	
	      // for variable "data"
	      com.usu.tinyservice.network.tests.Data1[] datas = new com.usu.tinyservice.network.tests.Data1[reqMsg.inParams[1].values.length];
	      for (int i = 0; i < reqMsg.inParams[1].values.length; i++) {
	        datas[i] = (com.usu.tinyservice.network.tests.Data1) reqMsg.inParams[1].values[i];
	      }
	      com.usu.tinyservice.network.tests.Data1[] data = datas;
	
	      // for variable "fileOnly"
	      boolean[] fileOnlys = new boolean[reqMsg.inParams[2].values.length];
	      for (int i = 0; i < reqMsg.inParams[2].values.length; i++) {
	        fileOnlys[i] = (boolean) reqMsg.inParams[2].values[i];
	      }
	      boolean fileOnly = fileOnlys[0];
	
	      // start calling function "getFileList1"
	      com.usu.tinyservice.network.tests.Data1[] rets = mobileservicedemo.getFileList1(path, data, fileOnly);
	      String retType = "com.usu.tinyservice.tests.Data1[]";
	      ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, rets);
	
	      // convert to binary array
	      respBytes = NetUtils.serialize(respMsg);
	      break;
	    }
	    }
	    
	    return respBytes;
	  }
		
	  @Override
	  public void receivedTask(String clientId, int dataSize) {
	  }
	  
	  @Override
	  public void workerStarted(String workerId) {
	  }
		
	  @Override
	  public void workerFinished(String workerId, TaskDone taskDone) {
	  }
	  
	};
  }

}
