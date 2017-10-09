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

  public MobileServiceDemoClient() {
    // start listener
    this.listener = listener;

    // create request message and send
    req = new RequesterX();
    req.start();
  }

  class RequesterX extends Requester {
	@Override
	public void receive(byte[] resp) {
	  listener.dataReceived(resp);
	}
  }
}
