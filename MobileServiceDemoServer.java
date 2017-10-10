package com.usu.tinyservice.classes;

import com.usu.tinyservice.messages.JsonRequestMessage;
import com.usu.tinyservice.messages.JsonResponseMessage;
import com.usu.tinyservice.network.JSONHelper;
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
      // get request message from JSON 
      String reqJSON = new String(req);
      JsonRequestMessage reqMsg = JSONHelper.getRequest(reqJSON);

      switch (reqMsg.functionName) {
      case "getFileList1": {
        // for variable "path"
        String[] paths = new String[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          paths[i] = reqMsg.inParams[0].values[i];
        }
        String path = paths[0];

        // for variable "data"
        int[] datas = new int[reqMsg.inParams[1].values.length];
        for (int i = 0; i < reqMsg.inParams[1].values.length; i++) {
          datas[i] = Integer.parseInt(reqMsg.inParams[1].values[i]);
        }
        int[] data = datas;

        // for variable "fileOnly"
        boolean[] fileOnlys = new boolean[reqMsg.inParams[2].values.length];
        for (int i = 0; i < reqMsg.inParams[2].values.length; i++) {
          fileOnlys[i] = Boolean.parseBoolean(reqMsg.inParams[2].values[i]);
        }
        boolean fileOnly = fileOnlys[0];

        // start calling function "getFileList1"
        int[] rets = mobileservicedemo.getFileList1(path, data, fileOnly);
        String retType = "int";
        String[] retValues = NetUtils.getStringArray(rets);
        JsonResponseMessage respMsg = new JsonResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, retValues);

        // convert to JSON
        String respJSON = JSONHelper.createResponse(respMsg);
        send(respJSON);
        break;
      }
      }

    }
  }
}
