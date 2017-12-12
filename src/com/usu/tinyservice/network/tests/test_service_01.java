package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.network.Broker;

public class test_service_01 extends Thread {
	public void run() {
		new Broker();

		new MobileServiceDemoWorker();
	}
	
	public static void main(String[] args) {
		new test_service_01().start();
	}
}
