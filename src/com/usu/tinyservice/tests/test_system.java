package com.usu.tinyservice.tests;

import com.usu.tinyservice.classes.Data1;
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
		Data1 data1 = new Data1(); 
		data1.data11 = new int[] { 1, 2, 3 };
		data1.data12 = new String[] { "abc", "def" };
		data1.data13 = new byte[] { 1, 1, 0 };
		
		client.getFileList1("D:\\", new Data1[] { data1 }, true);
	}
	
	public static void main(String args[]) {
		new test_system().start();
	}
}
