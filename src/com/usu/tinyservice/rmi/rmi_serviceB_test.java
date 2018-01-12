package com.usu.tinyservice.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class rmi_serviceB_test extends Thread {
	public void run() {
		try {
			Registry registry = LocateRegistry.createRegistry(1099);
			RmiServiceB service = (RmiServiceB) registry.lookup("129.123.7.172");
			// RmiServiceB service = (RmiServiceB) Naming.lookup("//localhost/ServiceB");
			
			int size = 0;
			byte[] data;
			for (int i = 0; i < 1024; i++) {
				size = i * 1024;
				
				data = new byte[size];
				for (int j = 0; j < size; j++) {
					data[j] = (byte) (Math.random() * 127);
				}
				service.sendData(new String(data));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new rmi_serviceB_test().start();
	}
}
