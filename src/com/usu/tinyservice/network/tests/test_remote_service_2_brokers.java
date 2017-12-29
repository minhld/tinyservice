package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.Bridge;
import com.usu.tinyservice.network.Broker;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;

/**
 * REMOTE SERVICE TEST SUITE - BROKER #2 
 * ------
 * START 3 BROKERS: 1 as a local Broker, one is the second remote Broker
 * located on the local computer and a remote Broker (providing remote
 * services) 
 * 
 * - this one will create a new remote Broker running on local computer
 * to mimic the second remote Broker (with different port). This Broker
 * will be considered as a bridge between the local Broker and the final
 * remote Broker {@link test_remote_service_remote_broker} 
 * acclaimer: the {@link test_remote_service_remote_broker} should be started first
 * 
 * @author minhld
 *
 */
public class test_remote_service_2_brokers extends Thread {
	public void run() {
		String remoteServer = "129.123.7.41";
		int remoteClientPort = 5555;
		int remoteWorkerPort = 5556;
		
		// start a remote Broker running on the local computer to mimic
		// the second remote Broker. It will listen on the port 5555 for
		// Clients and 5556 for Workers
		new Broker(NetUtils.DEFAULT_IP, remoteClientPort, remoteWorkerPort);

		NetUtils.sleep(1000);
		
		new Broker();
		
		
		// start a local Broker with default parameters
		// also start the Bridge to bridge between the local server with the
		// local-remote one
		
		
		NetUtils.sleep(1000);
		
		new Bridge(NetUtils.DEFAULT_IP, remoteWorkerPort, remoteServer, NetUtils.CLIENT_PORT);
		
		NetUtils.sleep(1000);
		
		new Bridge(NetUtils.DEFAULT_IP, NetUtils.WORKER_PORT, NetUtils.DEFAULT_IP, remoteClientPort);

		// wait until all Brokers, Bridges and Workers all started
		// NetUtils.sleep(1000);
		
		// startClient();
	}
	
	void startClient() {
		MobileServiceDemoClient client = new MobileServiceDemoClient(new ReceiveListener() {
			@Override
			public void dataReceived(String idChain, String funcName, byte[] data) {
				ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
				if (resp.functionName.equals(NetUtils.BROKER_INFO)) {
					// a denied message from the Broker
					String msg = (String) resp.outParam.values[0];
					NetUtils.printX("[Client] Error " + msg);
					
				} else if (resp.functionName.equals("getFileList1")) {
					com.usu.tinyservice.network.tests.Data1[] data1 = (com.usu.tinyservice.network.tests.Data1[]) resp.outParam.values;
					NetUtils.print("[Client] Received: ");
					NetUtils.print(new String(data1[0].data13));
					
				} else if (resp.functionName.equals("getFileList2")) {
					java.lang.String[] files = (java.lang.String[]) resp.outParam.values;
					NetUtils.print("[Client] Received: ");
					for (int i = 0; i < files.length; i++) {
						NetUtils.print("\t File: " + files[i]);
					}
				}
			}
		});
		
		// prepare data
		com.usu.tinyservice.network.tests.Data1 data1 = new com.usu.tinyservice.network.tests.Data1(); 
		data1.data11 = new int[] { 1, 2, 3 };
		data1.data12 = new String[] { "abc", "def" };
		data1.data13 = "hello from client".getBytes();
		
		// client.getFileList1("D:\\", new Data1[] { data1 }, true);
		// client.getFileList2("D:\\");
		// client.getFileList1("D:\\", new Data1[] { data1 }, true);
		client.getFileList2("/");
	}
	
	public static void main(String args[]) {
		new test_remote_service_2_brokers().start();
	}
}
