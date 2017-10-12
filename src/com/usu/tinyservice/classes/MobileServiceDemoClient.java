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


public void getFileList1(String path, Data1 data[], boolean fileOnly) {
    // compose input parameters
    String functionName = "getFileList1";
    String outType = "Data1[]";
    RequestMessage reqMsg = new RequestMessage(functionName, outType);
    
    // create request message and send
    reqMsg.inParams = new InParam[3];
    
    reqMsg.inParams[0] = new InParam("path", "String", path);
    reqMsg.inParams[1] = new InParam("data", "Data1[]", data);
    reqMsg.inParams[2] = new InParam("fileOnly", "Boolean", fileOnly);

    // create a binary message
    byte[] msg = NetUtils.serialize(reqMsg);
    req.send(msg);
  }


  class RequesterX extends Requester {
	@Override
	public void receive(byte[] resp) {
	  listener.dataReceived(resp);
	}
  }
}
