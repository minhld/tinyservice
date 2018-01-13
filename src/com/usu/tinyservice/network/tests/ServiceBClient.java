package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.InParam;
import com.usu.tinyservice.messages.binary.RequestMessage;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;
import com.usu.tinyservice.network.Client;

public class ServiceBClient {
  ReceiveListener listener;
  RmiClient client;

  public ServiceBClient(ReceiveListener listener) {
    // start listener
    this.listener = listener;

    // create request message and send
    client = new RmiClient();
  }

  public ServiceBClient(String brokerIp, ReceiveListener listener) {    // start listener
    this.listener = listener;

    // create request message and send
    client = new RmiClient(brokerIp);
  }

  public void sendData(java.lang.String msg) {
    // compose input parameters
    String functionName = "sendData";
    String outType = "java.lang.String[]";
    RequestMessage reqMsg = new RequestMessage(functionName, outType);
    
    // create request message and send
    reqMsg.inParams = new InParam[1];
    reqMsg.inParams[0] = new InParam("msg", "java.lang.String", msg);

    // create a binary message
    byte[] reqBytes = NetUtils.serialize(reqMsg);
    client.send(functionName, reqBytes);
  }

  public void sendData2(java.lang.String msg) {
    // compose input parameters
    String functionName = "sendData2";
    String outType = "java.lang.String[]";
    RequestMessage reqMsg = new RequestMessage(functionName, outType);
    
    // create request message and send
    reqMsg.inParams = new InParam[1];
    reqMsg.inParams[0] = new InParam("msg", "java.lang.String", msg);

    // create a binary message
    byte[] reqBytes = NetUtils.serialize(reqMsg);
    client.send(functionName, reqBytes);
  }

  public void getFolderList(java.lang.String path) {
    // compose input parameters
    String functionName = "getFolderList";
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
