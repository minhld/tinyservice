package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.Bridge;
import com.usu.tinyservice.network.Broker;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;

public class test_service_03 extends Thread {
	public void run() {
		String remoteBrokerIp = "129.123.7.41";
		
		new Broker();

		new Bridge(remoteBrokerIp);
		
		MobileServiceDemoClient client = new MobileServiceDemoClient(new ReceiveListener() {
			@Override
			public void dataReceived(String idChain, String funcName, byte[] data) {
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
		
		// prepare data
		com.usu.tinyservice.network.tests.Data1 data1 = new com.usu.tinyservice.network.tests.Data1(); 
		data1.data11 = new int[] { 1, 2, 3 };
		data1.data12 = new String[] { "abc", "def" };
		data1.data13 = "hello from client".getBytes();
		
		NetUtils.sleep(1000);

		// client.getFileList1("D:\\", new Data1[] { data1 }, true);
		client.getFileList2("D:\\");
		// new MobileServiceDemoWorker();

		// new MobileServiceDemoWorker3(remoteBrokerIp);
	}
	
	public static void main(String[] args) {
		new test_service_03().start();
	}
}
