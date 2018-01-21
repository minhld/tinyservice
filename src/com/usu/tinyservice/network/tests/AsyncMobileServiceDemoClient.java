package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.InParam;
import com.usu.tinyservice.messages.binary.RequestMessage;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;
import com.usu.tinyservice.network.Client;

public class AsyncMobileServiceDemoClient {
  ReceiveListener listener;
  RmiClient client;

  public AsyncMobileServiceDemoClient(ReceiveListener listener) {
    // start listener
    this.listener = listener;

    // create request message and send
    client = new RmiClient();
  }

  public void getFileList1(java.lang.String path, com.usu.tinyservice.network.tests.Data1[] data, boolean fileOnly) {
    // compose input parameters
    String functionName = "getFileList1";
    String outType = "com.usu.tinyservice.network.tests.Data1[]";
    RequestMessage reqMsg = new RequestMessage(functionName, outType);
    
    // create request message and send
    reqMsg.inParams = new InParam[3];
    reqMsg.inParams[0] = new InParam("path", "java.lang.String", path);
    reqMsg.inParams[1] = new InParam("data", "com.usu.tinyservice.network.tests.Data1[]", data);
    reqMsg.inParams[2] = new InParam("fileOnly", "boolean", fileOnly);

    // create a binary message
    byte[] reqBytes = NetUtils.serialize(reqMsg);
    client.send(functionName, reqBytes);
  }

  public void getFileList2(java.lang.String path) {
    // compose input parameters
    String functionName = "getFileList2";
    String outType = "java.lang.String[]";
    RequestMessage reqMsg = new RequestMessage(functionName, outType);
    
    // create request message and send
    reqMsg.inParams = new InParam[1];
    reqMsg.inParams[0] = new InParam("path", "java.lang.String", path);

    // create a binary message
    byte[] reqBytes = NetUtils.serialize(reqMsg);
    client.send(functionName, reqBytes);
  }


  class RmiClient extends Client {
	public RmiClient() {
	  super();
	}
	
	public RmiClient(String brokerIp) {
	  super(brokerIp);
	}
	
	@Override
	public void receive(String idChain, String funcName, byte[] resp) {
	  listener.dataReceived(idChain, funcName, resp);
	}
  }
}
