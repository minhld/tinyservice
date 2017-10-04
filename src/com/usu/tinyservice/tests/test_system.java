package com.usu.tinyservice.tests;

import com.usu.tinyservice.classes.MobileServiceDemoXClient;
import com.usu.tinyservice.classes.MobileServiceDemoXServer;
import com.usu.tinyservice.network.ReceiveListener;
import com.usu.tinyservice.utils.Utils;

public class test_system extends Thread {
	public void run() {
		// start a server
		new MobileServiceDemoXServer();
		
		// start a client
		MobileServiceDemoXClient client = new MobileServiceDemoXClient(new ReceiveListener() {
			@Override
			public void dataReceived(byte[] data) {
				System.out.println(new String(data));
			}
		});
		
		Utils.sleep(500);
		
		client.getRoot();
	}
	
	public static void main(String args[]) {
		new test_system().start();
	}
}
