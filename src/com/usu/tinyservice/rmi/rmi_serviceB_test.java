package com.usu.tinyservice.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class rmi_serviceB_test extends Thread {
	public void run() {
		String ip = "129.123.7.172";
		String serviceName = "ServiceB";
		
		try {
			
			Registry registry = LocateRegistry.getRegistry(ip, 1099);
			RmiServiceB service = (RmiServiceB) registry.lookup(serviceName);
			
			int size = 0;
			byte[] data;
			for (int i = 0; i < 1024; i++) {
				size = i * 1024;
				
				data = new byte[size];
				for (int j = 0; j < size; j++) {
					data[j] = (byte) (Math.random() * 127);
				}
				String[] rets = service.sendData(new String(data));
				
				if (rets.length > 0) {
					System.out.println(rets[0] + ": " + rets[1]);
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new rmi_serviceB_test().start();
	}
}
