package com.usu.tinyservice.tests;

import com.usu.tinyservice.network.Broker;
import com.usu.tinyservice.network.Client;
import com.usu.tinyservice.network.Worker;

public class test_client_worker extends Thread {
	public void run() {
		new Broker();
		
		new Worker() {
			@Override
			public byte[] resolveRequest(byte[] packageBytes) {
				return ("[" + workerId + "] Resolve Request").getBytes();
			}
			
			@Override
			public void receivedTask(String clientId, int dataSize) {
				System.out.println("[" + clientId + "] With Size: " + dataSize);
			}
		};
		
	}
	
	public static void main(String[] args) {
		new test_client_worker().start();
	}
}
