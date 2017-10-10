package com.usu.tinyservice.classes;

import com.usu.tinyservice.messages.JsonInParam;
import com.usu.tinyservice.messages.JsonRequestMessage;
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


  public void getFileList1(String path, int[] data, boolean fileOnly) {
    // compose input parameters
    String functionName = "getFileList1";
    String outType = "int[]";
    JsonRequestMessage reqMsg = new JsonRequestMessage(functionName, outType);
    
    // create request message and send
    reqMsg.inParams = new JsonInParam[3];
    String[] param1 = NetUtils.getStringArray(path);
    reqMsg.inParams[0] = new JsonInParam("path", "String", param1);
    String[] param2 = NetUtils.getStringArray(data);
    reqMsg.inParams[1] = new JsonInParam("data", "int[]", param2);
    String[] param3 = NetUtils.getStringArray(fileOnly);
    reqMsg.inParams[2] = new JsonInParam("fileOnly", "boolean", param3);

    // create a json message
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
