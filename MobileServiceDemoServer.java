package com.usu.tinyservice.classes;

import com.usu.tinyservice.messages.RequestMessage;
import com.usu.tinyservice.messages.ResponseMessage;
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
      RequestMessage reqMsg = JSONHelper.getRequest(reqJSON);

      switch (reqMsg.functionName) {
      case "getFileList": {
        // for variable "path"
        String[] paths = new String[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          paths[i] = reqMsg.inParams[0].values[i];
        }
        String path = paths[0];

        // for variable "fileOnly"
        boolean[] fileOnlys = new boolean[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          fileOnlys[i] = Boolean.parseBoolean(reqMsg.inParams[0].values[i]);
        }
        boolean fileOnly = fileOnlys[0];

        // start calling function "getFileList"
        java.lang.String[] rets = mobileservicedemo.getFileList(path, fileOnly);
        String retType = "String";
        String[] retValues = NetUtils.getStringArray(rets);
        ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, retValues);

        // convert to JSON
        String respJSON = JSONHelper.createResponse(respMsg);
        send(respJSON);
        break;
      }
      case "getFileList2": {
        // for variable "path"
        String[] paths = new String[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          paths[i] = reqMsg.inParams[0].values[i];
        }
        String path = paths[0];

        // for variable "count"
        int[] counts = new int[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          counts[i] = Integer.parseInt(reqMsg.inParams[0].values[i]);
        }
        int[] count = counts;

        // for variable "fileOnly"
        boolean[] fileOnlys = new boolean[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          fileOnlys[i] = Boolean.parseBoolean(reqMsg.inParams[0].values[i]);
        }
        boolean fileOnly = fileOnlys[0];

        // start calling function "getFileList2"
        int[] rets = mobileservicedemo.getFileList2(path, count, fileOnly);
        String retType = "int";
        String[] retValues = NetUtils.getStringArray(rets);
        ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, retValues);

        // convert to JSON
        String respJSON = JSONHelper.createResponse(respMsg);
        send(respJSON);
        break;
      }
      }

    }
  }
}
