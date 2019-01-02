package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.*;
import com.usu.tinyservice.network.parsers.ByteDataParser;

/**
 * REMOTE SERVICE TEST SUITE - REMOTE SERVICE CLIENT
 * ------
 * - this test is designed to test at Walmart
 * - it includes 2 brokers and one bridge
 * 
 * @author minhld
 * @version Nov 21, 2018
 *
 */
public class test_mbroker_vdm_1 extends Thread {
	public void run() {
		// start a remote broker
		// listen to client 3334 and worker 3333
		// start a remote worker
		String remoteBrokerIp = "192.168.0.103";		// home
		// String remoteBrokerIp = "172.21.13.208";		// walmart
		
		new MBrokerX(remoteBrokerIp, 3334, 3333);
		new MobileServiceDemoWorker(remoteBrokerIp, 3333);
		
		// start a local broker
		// listen to client 6668 and worker 6666
		new MBrokerX();
        new MobileServiceDemoWorker(remoteBrokerIp, 6666);

		NetUtils.sleep(1000);
		
		new Bridge(remoteBrokerIp, 6666, remoteBrokerIp, 3334);
		
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
				} else if (resp.functionName.equals("sendData")) {
					byte[] respStr = (byte[]) resp.outParam.values[0];
					NetUtils.print("[Client] Received: " + new String(respStr));
				}
			}
		});
		
		NetUtils.sleep(500);

		ByteDataParser dataParser = new ByteDataParser();
		byte[] data = (byte[]) dataParser.loadObject("1");
		client.sendData(data);
		
	}
	
	public static void main(String[] args) {
		new test_mbroker_vdm_1().start();
	}
}
