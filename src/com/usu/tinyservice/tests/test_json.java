package com.usu.tinyservice.tests;

import com.usu.tinyservice.messages.InParam;
import com.usu.tinyservice.messages.OutParam;
import com.usu.tinyservice.messages.RequestMessage;
import com.usu.tinyservice.network.JSONHelper;

public class test_json extends Thread {
	public void run() {
		// REQUEST 1
		RequestMessage request1 = new RequestMessage();
		request1.funcName = "getRoot";
		request1.outParam = new OutParam("String");
		
		String request1Json = JSONHelper.createRequest(request1);
		System.out.println(request1Json);
		
		request1 = JSONHelper.getRequest(request1Json);
		System.out.println("funcName: " + request1.funcName);
		for (int i = 0; i < request1.inParams.length; i++) {
			System.out.println("inParams[" + i + "]: " + request1.inParams[i].param + "," + request1.inParams[i].values[0]);
		}
		
		// REQUEST 2
		RequestMessage request2 = new RequestMessage();
		request2.funcName = "getFileList";
		request2.outParam = new OutParam("String");
		request2.inParams = new InParam[2];
		request2.inParams[0] = new InParam("path", "String", new String[] { "D:\\" });
		request2.inParams[1] = new InParam("fileOnly", "boolean", new String[] { "true" });
		request2.outParam = new OutParam("String[]");
		
		String request2Json = JSONHelper.createRequest(request2);
		System.out.println(request2Json);
		
		request2 = JSONHelper.getRequest(request2Json);
		System.out.println("funcName: " + request2.funcName);
		for (int i = 0; i < request2.inParams.length; i++) {
			System.out.println("inParams[" + i + "]: " + request2.inParams[i].param + "," + request2.inParams[i].values[0]);
		}
		System.out.println("outParam: " + request2.outParam.type);
	}
	
	public static void main(String args[]) {
		new test_json().start();
	}
}
