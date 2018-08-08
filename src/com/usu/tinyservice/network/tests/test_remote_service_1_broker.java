package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.Bridge;
import com.usu.tinyservice.network.Broker;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;

/**
 * REMOTE SERVICE TEST SUITE - REMOTE SERVICE CLIENT
 * ------
 * - this test is the client part of REMOTE BROKER test suite: this will create
 * a local Broker to talk to the local clients, a Bridge to talk to the remote
 * Broker and a Client to request the local Broker.
 * - this will connect to the REMOTE BROKER (129.123.7.41) which is started by 
 * {@link test_remote_service_remote_broker}
 * - acclaimer: this should be started after {@link test_remote_service_remote_broker}
 * 
 * 
 * @author minhld
 *
 */
public class test_remote_service_1_broker extends Thread {
	public void run() {
		String remoteBrokerIp = "192.168.0.104";
		
		new Broker();

		new Bridge(remoteBrokerIp);
		
		NetUtils.sleep(1000);

		startClient();
	}
	
	
	private void startClient() {
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
		
		// new MobileServiceDemoWorker();

		// new MobileServiceDemoWorker3(remoteBrokerIp);
	}
	
	public static void main(String[] args) {
		new test_remote_service_1_broker().start();
	}
}
