package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.Broker;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;

public class test_service_01 extends Thread {
	public void run() {
		new Broker();

		new MobileServiceDemoWorker();
		
		MobileServiceDemoClient client = new MobileServiceDemoClient(new ReceiveListener() {
			@Override
			public void dataReceived(byte[] data) {
				ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
				Data1[] data1 = (Data1[]) resp.outParam.values;
				System.out.println(new String(data1[0].data13));
			}
		});
		
		NetUtils.sleep(500);
		
		// prepare data
		com.usu.tinyservice.network.tests.Data1 data1 = new com.usu.tinyservice.network.tests.Data1(); 
		data1.data11 = new int[] { 1, 2, 3 };
		data1.data12 = new String[] { "abc", "def" };
		data1.data13 = "hello from client".getBytes();

		client.getFileList1("D:\\", new Data1[] { data1 }, true);
		
		for (int i = 0; i < 5; i++) {
			NetUtils.sleep(1500);
			
			client.getFileList1("D:\\", new Data1[] { data1 }, true);
		}
	}
	
	public static void main(String[] args) {
		new test_service_01().start();
	}
}