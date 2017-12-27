package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.network.Broker;

/**
 * REMOTE SERVICE TEST SUITE - REMOTE BROKER 
 * (WITH WORKERS PROVIDING REMOTE SERVICES)
 * ------
 * - this test locates on a different server (129.123.7.41)
 * - this provides remote services to the others
 * - acclaimer: this should be started firstly 
 * 
 * @author minhld
 *
 */
public class test_remote_broker_01 extends Thread {
	public void run() {
		String brokerIp = "129.123.7.41";
		
		new Broker(brokerIp);

		new MobileServiceDemoWorker3(brokerIp);
	}
	
	public static void main(String[] args) {
		new test_remote_broker_01().start();
	}
}
