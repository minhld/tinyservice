package com.usu.tinyservice.tests;

import com.usu.tinyservice.classes.MobileServiceDemoClient;
import com.usu.tinyservice.classes.MobileServiceDemoServer;
import com.usu.tinyservice.network.ReceiveListener;
import com.usu.tinyservice.utils.Utils;

public class test_system extends Thread {
	public void run() {
		// start a server
		new MobileServiceDemoServer();
		
		// start a client
		MobileServiceDemoClient client = new MobileServiceDemoClient(new ReceiveListener() {
			@Override
			public void dataReceived(byte[] data) {
				System.out.println(new String(data));
			}
		});
		
		Utils.sleep(500);
		
		int[] count = new int[] { 1, 3, 5, 7, 9 };
		client.getFileList1("D:\\", count, true);
	}
	
	public static void main(String args[]) {
		new test_system().start();
	}
}
