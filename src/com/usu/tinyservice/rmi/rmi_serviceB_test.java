package com.usu.tinyservice.rmi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class rmi_serviceB_test extends Thread {
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
			for (int i = 0; i < 1024; i += 16) {
				size = i * 1024;
				
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
			

			
//			int[] sizes = new int[] { 1, 1, 1, 1, 1, 10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1024 };
//			byte[] data;
//			for (int i = 0; i < sizes.length; i++) {
//				sizes[i] = sizes[i] * 1024;
//				data = new byte[sizes[i]];
//				for (int j = 0; j < sizes[i]; j++) {
//					data[j] = (byte) (Math.random() * 127);
//				}
//				
//				startTime = System.nanoTime();
//				
//				String[] rets = service.sendData(new String(data));
//				
//				totalTime = ((double) (System.nanoTime() - startTime)) / Math.pow(10, 6);
//				
//				if (rets.length > 0) {
//					System.out.println(rets[0] + rets[1] + " in " + totalTime + "ms");
//                	
//					writer.write("" + totalTime + "\r\n");
//                	writer.flush();
//				}
//			}

//			long startTime = 0;
//			double totalTime = 0;
//			
//			byte[] data;
//			int size = 1024 * 1024;
//			for (int i = 0; i < 1000; i++) {
//				data = new byte[size];
//				for (int j = 0; j < size; j++) {
//					data[j] = (byte) (Math.random() * 127);
//				}
//				
//				startTime = System.nanoTime();
//				
//				String[] rets = service.sendData(new String(data));
//				
//				totalTime = ((double) (System.nanoTime() - startTime)) / Math.pow(10, 6);
//				
//				if (rets.length > 0) {
//					System.out.println(rets[0] + rets[1] + " in " + totalTime + "ms");
//                	
//					writer.write("" + totalTime + "\r\n");
//                	writer.flush();
//				}
//			}

			
			writer.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new rmi_serviceB_test().start();
	}
}
