package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.RequestMessage;
import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.Worker;

public class ServiceBWorker {
  ServiceB serviceb;

  public ServiceBWorker() {
    this(NetUtils.DEFAULT_IP);
  }

  public ServiceBWorker(String brokerIp) {
    serviceb = new ServiceB();
    new WorkerX(brokerIp);
  }

  class WorkerX extends Worker {
    public WorkerX() {
      super();
    }

    public WorkerX(String brokerIp) {
      super(brokerIp);
    }


    @Override
    public byte[] resolveRequest(byte[] packageBytes) {
      byte[] respBytes = null;

      // get request message
      RequestMessage reqMsg = (RequestMessage) NetUtils.deserialize(packageBytes);

      switch (reqMsg.functionName) {
      case "sendData": {
        // for variable "msg"
        java.lang.String[] msgs = new java.lang.String[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          msgs[i] = (java.lang.String) reqMsg.inParams[0].values[i];
        }
        java.lang.String msg = msgs[0];

        // start calling function "sendData"
        java.lang.String[] rets = serviceb.sendData(msg);
        String retType = "java.lang.String[]";
        ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, rets);

        // convert to binary array
        respBytes = NetUtils.serialize(respMsg);
        break;
      }
      case "getFolderList": {
        // for variable "path"
        java.lang.String[] paths = new java.lang.String[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          paths[i] = (java.lang.String) reqMsg.inParams[0].values[i];
        }
        java.lang.String path = paths[0];

        // start calling function "getFolderList"
        java.lang.String[] rets = serviceb.getFolderList(path);
        String retType = "java.lang.String[]";
        ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, rets);

        // convert to binary array
        respBytes = NetUtils.serialize(respMsg);
        break;
      }
      }

      return respBytes;

    }

    @Override
    public String info() {
      String json =
        "{" +
          "\"code\" : \"REGISTER\"," +
          "\"id\" : \"" + workerId + "\"," +
          "\"functions\" : [" +
            "{" +
              "\"functionName\" : \"sendData\"," +
              "\"inParams\" : [\"java.lang.String\"]," +
              "\"outParam\" : \"java.lang.String[]\"" +
            "}," +
            "{" +
              "\"functionName\" : \"getFolderList\"," +
              "\"inParams\" : [\"java.lang.String\"]," +
              "\"outParam\" : \"java.lang.String[]\"" +
            "}" +
          "]" +
        "}";
      return json;
    }
  }
}
