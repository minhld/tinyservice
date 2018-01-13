package com.usu.tinyservice.network.tests;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.Bridge;
import com.usu.tinyservice.network.Broker;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;

public class test_remote_serviceB_client extends Thread {
	BufferedWriter writer;
	
	public void run() {
		String localBrokerIp = "*";
		String remoteBrokerIp = "129.123.7.41";
		
		new Broker(localBrokerIp);
		
		NetUtils.sleep(500);
		
		new Bridge(localBrokerIp, 6666, remoteBrokerIp, 6668);

		
		// save to file
		try {
			writer = new BufferedWriter(new FileWriter("result.txt", false));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		NetUtils.sleep(1000);
		
		startClient();
		
	}
	
	
	
	long startTime = 0;
	ServiceBClient client;
	
	private void startClient() {
		client = new ServiceBClient(new ReceiveListener() {
			@Override
			public void dataReceived(String idChain, String funcName, byte[] data) {
				ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
                if (resp.functionName.equals(NetUtils.BROKER_INFO)) {
                    // a denied message from the Broker
                    String msg = (String) resp.outParam.values[0];
                    NetUtils.printX(" Error " + msg);
                } else if (resp.functionName.equals("sendData")) {
                    String[] msgs = (String[]) resp.outParam.values;
                    double totalTime = (double) ((double) (System.nanoTime() - startTime) / Math.pow(10, 6));
                    NetUtils.print(msgs[0] + " " + msgs[1] + " in " + totalTime);
                    try {
                    	writer.write("" + totalTime + "\r\n");
                    	writer.flush();
                    } catch (Exception e) { }
                } else if (resp.functionName.equals("getFolderList")) {
                    String[] files = (String[]) resp.outParam.values;
                    NetUtils.print("[Client-" + client.client.clientId + "] Received: ");
                    for (int i = 0; i < files.length; i++) {
                    	NetUtils.print("\t File: " + files[i]);
                    }
                }
			}
		});
		
		NetUtils.sleep(1000);
		
//		int[] sizes = new int[] { 1, 1, 1, 1, 1, 10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1024 };
//		
//		byte[] data;
//		while (true) {
//			for (int i = 0; i < sizes.length; i++) {
//				data = new byte[sizes[i] * 1024];
//				for (int j = 0; j < sizes[i]; j++) {
//					data[j] = (byte) (Math.random() * 127);
//				}
//				
//				startTime = System.nanoTime();
//				
//				client.sendData(new String(data));
//				
//				NetUtils.sleep(300);
//	
//			}
//			
//			try {
//				// press a key to start again
//				System.out.print("Press a key to start again. ENTER to terminate: ");
//				int key = System.in.read();
//				if (key == 13) {
//					writer.close();
//					System.exit(0);
//				} 
//			} catch (Exception e) { }
//		}
		
		int size = 0;
		byte[] data;
		for (int i = 0; i < 1024; i += 16) {
			size = i * 1024;
			
			data = new byte[size];
			for (int j = 0; j < size; j++) {
				data[j] = (byte) (Math.random() * 127);
			}
			
			
			startTime = System.nanoTime();
			
			client.sendData(new String(data));
			
			NetUtils.sleep(300);
		}

	}
	
	public static void main(String args[]) {
		new test_remote_serviceB_client().start();
	}
}
