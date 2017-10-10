package com.usu.tinyservice.classes;

import com.usu.tinyservice.messages.InJsonParam;
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

  public void getFileList1(String path, Data1 data, boolean fileOnly) {
    // compose input parameters
    String functionName = "getFileList2";
    String outType = "Data1";
    RequestMessage reqMsg = new RequestMessage(functionName, outType);
    
    // create request message and send
    reqMsg.inParams = new InJsonParam[3];
    String[] param1 = NetUtils.getStringArray(path);
    reqMsg.inParams[0] = new InJsonParam("path", "String", param1);
    String[] param2 = NetUtils.getStringArray(count);
    reqMsg.inParams[1] = new InJsonParam("count", "int[]", param2);
    String[] param3 = NetUtils.getStringArray(fileOnly);
    reqMsg.inParams[2] = new InJsonParam("fileOnly", "boolean", param3);
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
