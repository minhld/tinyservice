package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.Broker;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;

public class test_service_01 extends Thread {
	public void run() {
		new Broker();

		new MobileServiceDemoWorker();
		
		new MobileServiceDemoWorker2();
		
		MobileServiceDemoClient client = new MobileServiceDemoClient(new ReceiveListener() {
			@Override
			public void dataReceived(byte[] data) {
				ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
				if (resp.functionName.equals(NetUtils.BROKER_INFO)) {
					// a denied message from the Broker
					String msg = (String) resp.outParam.values[0];
					System.err.println("[Client] Error " + msg);
					
				} else if (resp.functionName.equals("getFileList1")) {
					com.usu.tinyservice.network.tests.Data1[] data1 = (com.usu.tinyservice.network.tests.Data1[]) resp.outParam.values;
					System.out.println("[Client] Received: ");
					System.out.println(new String(data1[0].data13));
					
				} else if (resp.functionName.equals("getFileList2")) {
					java.lang.String[] files = (java.lang.String[]) resp.outParam.values;
					System.out.println("[Client] Received: ");
					for (int i = 0; i < files.length; i++) {
						System.out.println("\t File: " + files[i]);
					}
				}
			}
		});
		
		NetUtils.sleep(1000);
		
		// prepare data
		com.usu.tinyservice.network.tests.Data1 data1 = new com.usu.tinyservice.network.tests.Data1(); 
		data1.data11 = new int[] { 1, 2, 3 };
		data1.data12 = new String[] { "abc", "def" };
		data1.data13 = "hello from client".getBytes();

		client.getFileList1("D:\\", new Data1[] { data1 }, true);
		client.getFileList2("D:\\");
		
		for (int i = 0; i < 5; i++) {
			NetUtils.sleep(10);
			client.getFileList1("D:\\", new Data1[] { data1 }, true);
		}
	}
	
	public static void main(String[] args) {
		new test_service_01().start();
	}
}
