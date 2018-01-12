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
			
			/*
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
					System.out.println(rets[0] + rets[1]);
				}
			}
			*/
			
			int[] sizes = new int[] { 1, 1, 1, 1, 1, 10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1024 };
			
			long startTime = 0;
			double totalTime = 0;
			
			byte[] data;
			for (int i = 0; i < sizes.length; i++) {
				data = new byte[sizes[i]];
				for (int j = 0; j < sizes[i]; j++) {
					data[j] = (byte) (Math.random() * 127);
				}
				
				startTime = System.nanoTime();
				
				String[] rets = service.sendData(new String(data));
				
				totalTime = ((double) (System.nanoTime() - startTime)) / (10 * 10 * 10 * 10 * 10 * 10);
				
				
				
				if (rets.length > 0) {
					System.out.println(rets[0] + rets[1] + " in " + totalTime + "ms");
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
