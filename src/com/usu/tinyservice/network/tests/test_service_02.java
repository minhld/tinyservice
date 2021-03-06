package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;

public class test_service_02 extends Thread {
	public void run() {
		MobileServiceDemoClient client2 = new MobileServiceDemoClient(new ReceiveListener() {
			@Override
			public void dataReceived(String idChain, String funcName, byte[] data) {
				ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
				NetUtils.print("[Client] Received: ");
				
				if (resp.functionName.equals("getFileList1")) {
					com.usu.tinyservice.network.tests.Data1[] data1 = (com.usu.tinyservice.network.tests.Data1[]) resp.outParam.values;
					NetUtils.print(new String(data1[0].data13));
				} else if (resp.functionName.equals("getFileList2")) {
					java.lang.String[] files = (java.lang.String[]) resp.outParam.values;
					for (int i = 0; i < files.length; i++) {
						NetUtils.print("\t File: " + files[i]);
					}
				}
			}
		});
		
		NetUtils.sleep(500);
		
		// prepare data
		com.usu.tinyservice.network.tests.Data1 data1 = new com.usu.tinyservice.network.tests.Data1(); 
		data1.data11 = new int[] { 1, 2, 3 };
		data1.data12 = new String[] { "abc", "def" };
		data1.data13 = "hello from client".getBytes();

		for (int i = 0; i < 5; i++) {
			// client2.getFileList1("D:\\", new Data1[] { data1 }, true);
			client2.getFileList2("D:\\");
		}
	}
		
	
	public static void main(String[] args) {
		new test_service_02().start();
	}
}
