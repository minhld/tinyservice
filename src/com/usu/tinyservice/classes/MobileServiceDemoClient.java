package com.usu.tinyservice.classes;

import com.usu.tinyservice.messages.InParam;
import com.usu.tinyservice.messages.RequestMessage;
import com.usu.tinyservice.network.JSONHelper;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;
import com.usu.tinyservice.network.Requester;

public class MobileServiceDemoClient {
  public ReceiveListener listener;
  private RequesterX req;

  public MobileServiceDemoClient(ReceiveListener listener) {
    // start listener
    this.listener = listener;

    // create request message and send
    req = new RequesterX();
    req.start();
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
    
    // create request message and send
    reqMsg.inParams = new InParam[2];
    String[] param1 = NetUtils.getStringArray(path);
    reqMsg.inParams[0] = new InParam("path", "String", param1);
    String[] param2 = NetUtils.getStringArray(fileOnly);
    reqMsg.inParams[1] = new InParam("fileOnly", "boolean", param2);
    String msgJSON = JSONHelper.createRequest(reqMsg);
    req.send(msgJSON);
  }

  public void getFileList2(String path, int[] count, boolean fileOnly) {
    // compose input parameters
    String functionName = "getFileList2";
    String outType = "int[]";
    RequestMessage reqMsg = new RequestMessage(functionName, outType);
    
    // create request message and send
    reqMsg.inParams = new InParam[3];
    String[] param1 = NetUtils.getStringArray(path);
    reqMsg.inParams[0] = new InParam("path", "String", param1);
    String[] param2 = NetUtils.getStringArray(count);
    reqMsg.inParams[1] = new InParam("count", "int[]", param2);
    String[] param3 = NetUtils.getStringArray(fileOnly);
    reqMsg.inParams[2] = new InParam("fileOnly", "boolean", param3);
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
