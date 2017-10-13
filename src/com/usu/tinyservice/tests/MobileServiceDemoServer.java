package com.usu.tinyservice.tests;

import com.usu.tinyservice.messages.binary.RequestMessage;
import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.Responder;

public class MobileServiceDemoServer {
  MobileServiceDemo mobileservicedemo;
  ResponderX resp;

  public MobileServiceDemoServer() {
    mobileservicedemo = new MobileServiceDemo();
    resp = new ResponderX();
    resp.start();
  }

  class ResponderX extends Responder {
    @Override
    public void respond(byte[] req) {
      // get request message
      RequestMessage reqMsg = (RequestMessage) NetUtils.deserialize(req);

      switch (reqMsg.functionName) {
      case "getFileList1": {
        // for variable "path"
        java.lang.String[] paths = new java.lang.String[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          paths[i] = (java.lang.String) reqMsg.inParams[0].values[i];
        }
        java.lang.String path = paths[0];

        // for variable "data"
        com.usu.tinyservice.tests.Data1[] datas = new com.usu.tinyservice.tests.Data1[reqMsg.inParams[1].values.length];
        for (int i = 0; i < reqMsg.inParams[1].values.length; i++) {
          datas[i] = (com.usu.tinyservice.tests.Data1) reqMsg.inParams[1].values[i];
        }
        com.usu.tinyservice.tests.Data1[] data = datas;

        // for variable "fileOnly"
        boolean[] fileOnlys = new boolean[reqMsg.inParams[2].values.length];
        for (int i = 0; i < reqMsg.inParams[2].values.length; i++) {
          fileOnlys[i] = (boolean) reqMsg.inParams[2].values[i];
        }
        boolean fileOnly = fileOnlys[0];

        // start calling function "getFileList1"
        com.usu.tinyservice.tests.Data1[] rets = mobileservicedemo.getFileList1(path, data, fileOnly);
        String retType = "com.usu.tinyservice.tests.Data1[]";
        ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, rets);

        // convert to binary array
        byte[] respBytes = NetUtils.serialize(respMsg);
        send(respBytes);
        break;
      }
      }

    }
  }
}
