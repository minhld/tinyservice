package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.RequestMessage;
import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.Worker;

public class MobileServiceDemoWorker {
  MobileServiceDemo mobileservicedemo;

  public MobileServiceDemoWorker() {
    this(NetUtils.DEFAULT_IP);
  }

  public MobileServiceDemoWorker(String brokerIp) {
    mobileservicedemo = new MobileServiceDemo();
    new WorkerX(brokerIp);
  }

  public MobileServiceDemoWorker(String brokerIp, int workerPort) {
    mobileservicedemo = new MobileServiceDemo();
    new WorkerX(brokerIp, workerPort);
  }

  class WorkerX extends Worker {
    public WorkerX() {
      super();
    }

    public WorkerX(String brokerIp) {
      super(brokerIp);
    }

    public WorkerX(String brokerIp, int workerPort) {
      super(brokerIp, workerPort);
    }


    @Override
    public byte[] resolveRequest(byte[] packageBytes) {
      byte[] respBytes = null;

      // get request message
      RequestMessage reqMsg = (RequestMessage) NetUtils.deserialize(packageBytes);

      switch (reqMsg.functionName) {
      case "getFileList1": {
        // for variable "path"
        java.lang.String[] paths = new java.lang.String[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          paths[i] = (java.lang.String) reqMsg.inParams[0].values[i];
        }
        java.lang.String path = paths[0];

        // for variable "data"
        com.usu.tinyservice.network.tests.Data1[] datas = new com.usu.tinyservice.network.tests.Data1[reqMsg.inParams[1].values.length];
        for (int i = 0; i < reqMsg.inParams[1].values.length; i++) {
          datas[i] = (com.usu.tinyservice.network.tests.Data1) reqMsg.inParams[1].values[i];
        }
        com.usu.tinyservice.network.tests.Data1[] data = datas;

        // for variable "fileOnly"
        boolean[] fileOnlys = new boolean[reqMsg.inParams[2].values.length];
        for (int i = 0; i < reqMsg.inParams[2].values.length; i++) {
          fileOnlys[i] = (boolean) reqMsg.inParams[2].values[i];
        }
        boolean fileOnly = fileOnlys[0];

        // start calling function "getFileList1"
        com.usu.tinyservice.network.tests.Data1[] rets = mobileservicedemo.getFileList1(path, data, fileOnly);
        String retType = "com.usu.tinyservice.network.tests.Data1[]";
        ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, rets);

        // convert to binary array
        respBytes = NetUtils.serialize(respMsg);
        break;
      }
      case "getFileList2": {
        // for variable "path"
        java.lang.String[] paths = new java.lang.String[reqMsg.inParams[0].values.length];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          paths[i] = (java.lang.String) reqMsg.inParams[0].values[i];
        }
        java.lang.String path = paths[0];

        // start calling function "getFileList2"
        java.lang.String[] rets = mobileservicedemo.getFileList2(path);
        String retType = "java.lang.String[]";
        ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, rets);

        // convert to binary array
        respBytes = NetUtils.serialize(respMsg);
        break;
      }
      case "sendData": {
        // for variable "data"
        byte[][] datas = new byte[reqMsg.inParams[0].values.length][];
        for (int i = 0; i < reqMsg.inParams[0].values.length; i++) {
          datas[i] = (byte[]) reqMsg.inParams[0].values[i];
        }
        byte[] data = datas[0];

        // start calling function "getFileList2"
        byte[] rets = mobileservicedemo.sendData(data);
        String retType = "byte[]";
        ResponseMessage respMsg = new ResponseMessage(reqMsg.messageId, reqMsg.functionName, retType, new Object[] { rets });

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
              "\"functionName\" : \"getFileList1\"," +
              "\"inParams\" : [\"java.lang.String\",\"com.usu.tinyservice.network.tests.Data1[]\",\"boolean\"]," +
              "\"outParam\" : \"com.usu.tinyservice.network.tests.Data1[]\"," +
              "\"workerInfos\" : [" +
              	"{" +
	              "\"workerId\" : \"" + workerId + "\"," +
	              "\"strength\" : " + strength() + "," +
	              "\"hops\" : 1" +
	            "}" +
              "]" +
            "}," +
            "{" +
              "\"functionName\" : \"getFileList2\"," +
              "\"inParams\" : [\"java.lang.String\"]," +
              "\"outParam\" : \"java.lang.String[]\"," +
              "\"workerInfos\" : [" +
            	"{" +
	              "\"workerId\" : \"" + workerId + "\"," +
	              "\"strength\" : " + strength() + "," +
	              "\"hops\" : 1" +
	            "}" +
	          "]" +
            "}," +
            "{" +
            "\"functionName\" : \"sendData\"," +
            "\"inParams\" : [\"byte[]\"]," +
            "\"outParam\" : \"byte[]\"," +
            "\"workerInfos\" : [" +
          	"{" +
	              "\"workerId\" : \"" + workerId + "\"," +
	              "\"strength\" : " + strength() + "," +
	              "\"hops\" : 1" +
	            "}" +
	          "]" +
            "}" +  
          "]" +
        "}";
      return json;
    }
    
    
  }
}
