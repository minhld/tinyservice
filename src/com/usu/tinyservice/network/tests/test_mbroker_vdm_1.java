package com.usu.tinyservice.network.tests;

import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.Bridge;
import com.usu.tinyservice.network.Broker;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;

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
		String remoteBrokerIp = "192.168.0.106";
		new Broker(remoteBrokerIp, 3334, 3333);
		new MobileServiceDemoWorker(remoteBrokerIp, 3333);
		
		// start a local broker
		// listen to client 6668 and worker 6666
		new Broker();

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
				}
			}
		});
		
		NetUtils.sleep(500);
		
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
		new test_mbroker_vdm_1().start();
	}
}
