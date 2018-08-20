package com.usu.tinyservice.network.tests;

import com.google.gson.Gson;
import com.usu.tinyservice.network.utils.Function;

public class test_service_03 extends Thread {
	public void run() {
		
		String json = "{\"functionName\":\"getFileList1\",\"inParams\":[\"java.lang.String\",\"com.usu.tinyservice.network.tests.Data1[]\",\"boolean\"],\"outParam\":\"com.usu.tinyservice.network.tests.Data1[]\",\"workerInfos\":[{\"workerId\":\"D82FB70A-75CEA6A1\",\"strength\":1.4362217,\"hops\":1}]}";
		// String json = "{\"functionName\":\"getFileList1\"}";
		// String json = "{\"functionName\":\"getFileList1\",\"inParams\":[\"java.lang.String\",\"com.usu.tinyservice.network.tests.Data1[]\",\"boolean\"],\"outParam\":\"com.usu.tinyservice.network.tests.Data1[]\" }";
		// String json = "{\"functionName\":\"getFileList1\",\"outParam\":\"com.usu.tinyservice.network.tests.Data1[]\" }";
		Gson gson = new Gson();
    	Function f = gson.fromJson(json, Function.class);
    	System.out.println(f.functionName);
	}
		
	
	public static void main(String[] args) {
		new test_service_03().start();
	}
}
