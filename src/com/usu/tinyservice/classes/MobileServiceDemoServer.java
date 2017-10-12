package com.usu.tinyservice.classes;

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
        String[] paths = new String[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          paths[i] = (String) reqMsg.inParams[0].values[i];
        }
        String path = paths[0];

        // for variable "data"
        Data1[] datas = new Data1[reqMsg.inParams[1].values.length];
        for (int i = 0; i < reqMsg.inParams[1].values.length; i++) {
          datas[i] = (Data1) reqMsg.inParams[1].values[i];
        }
        Data1[] data = datas;

        // for variable "fileOnly"
        boolean[] fileOnlys = new boolean[reqMsg.inParams[2].values.length];
        for (int i = 0; i < reqMsg.inParams[2].values.length; i++) {
          fileOnlys[i] = (Boolean) reqMsg.inParams[2].values[i];
        }
        boolean fileOnly = fileOnlys[0];

        // start calling function "getFileList1"
        Data1[] rets = mobileservicedemo.getFileList1(path, data, fileOnly);
        String respType = "Data1[]";
        ResponseMessage respMsg = new ResponseMessage(reqMsg.functionName, respType, rets);

        // convert to binary 
        byte[] resp = NetUtils.serialize(respMsg);
        send(resp);
        break;
      }
      }

    }
  }
}
