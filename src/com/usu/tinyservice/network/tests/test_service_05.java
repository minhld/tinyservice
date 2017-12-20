package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.network.Broker;

public class test_service_05 extends Thread {
	public void run() {
		String brokerIp = "129.123.7.41";
		
		new Broker(brokerIp);

		new MobileServiceDemoWorker3(brokerIp);
	}
	
	public static void main(String[] args) {
		new test_service_05().start();
	}
}
