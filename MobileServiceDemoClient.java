package com.usu.tinyservice.classes;

import com.usu.tinyservice.messages.binary.InParam;
import com.usu.tinyservice.messages.binary.RequestMessage;
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


  public void getFileList1(java.lang.String path, com.usu.tinyservice.tests.Data1[] data, boolean fileOnly) {
    // compose input parameters
    String functionName = "getFileList1";
    String outType = "com.usu.tinyservice.tests.Data1[]";
    RequestMessage reqMsg = new RequestMessage(functionName, outType);
    
    // create request message and send
    reqMsg.inParams = new InParam[3];
    reqMsg.inParams[0] = new InParam("path", "java.lang.String", path);
    reqMsg.inParams[1] = new InParam("data", "com.usu.tinyservice.tests.Data1[]", data);
    reqMsg.inParams[2] = new InParam("fileOnly", "boolean", fileOnly);

    // create a binary message
    byte[] reqBytes = NetUtils.serialize(reqMsg);
    req.send(reqBytes);
  }


  class RequesterX extends Requester {
	@Override
	public void receive(byte[] resp) {
	  listener.dataReceived(resp);
	}
  }
}
