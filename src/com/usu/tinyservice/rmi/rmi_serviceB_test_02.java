package com.usu.tinyservice.rmi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class rmi_serviceB_test_02 extends Thread {
	public void run() {
		String ip = "129.123.7.41";
		String serviceName = "ServiceB";
		BufferedWriter writer = null;

		try {
			
			Registry registry = LocateRegistry.getRegistry(ip, 1099);
			RmiServiceB service = (RmiServiceB) registry.lookup(serviceName);
			
			// save to file
			try {
				writer = new BufferedWriter(new FileWriter("rmi-result.txt", false));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			long startTime = 0;
			double totalTime = 0;
			
			int size = 0;
			byte[] data;
			for (int i = 0; i < 50; i++) {
				// different image size
				size = (int) (Math.random() * 25 * 1024);
				
				data = new byte[size];
				for (int j = 0; j < size; j++) {
					data[j] = (byte) (Math.random() * 127);
				}
				
				startTime = System.nanoTime();
				
				String[] rets = service.sendData(new String(data));
				
				totalTime = ((double) (System.nanoTime() - startTime)) / Math.pow(10, 6);
				
				if (rets.length > 0) {
					System.out.println(rets[0] + rets[1] + " in " + totalTime + "ms");
                	
					writer.write("" + totalTime + "\r\n");
                	writer.flush();
				}

			}

			writer.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new rmi_serviceB_test_02().start();
	}
}
