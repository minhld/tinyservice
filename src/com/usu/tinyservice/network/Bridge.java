package com.usu.tinyservice.network;

/**
 * Bridge is literally a bridge between two brokers. 
 * Bridge holds a worker to the current brokers to receive job
 * and holds a client to the remote broker to forward the job.
 * 
 * @author minhld
 *
 */
public class Bridge extends Thread {
	private String localBrokerIp = NetUtils.DEFAULT_IP;
	private String remoteBrokerIp = "";
	
	private Worker mWorker;
	private Client mClient;
	
	public Bridge(String localBrokerIp, String remoteBrokerIp) {
		this.localBrokerIp = localBrokerIp;
		this.remoteBrokerIp = remoteBrokerIp;
		this.start();
	}
	
	public void run() {
		// create a Worker to handle communication with the local broker
		// this Worker does nothing but forward the requests of the local
		// broker to the bridge's Client
		mWorker = new Worker(localBrokerIp) {
			@Override
			public byte[] resolveRequest(byte[] packageBytes) {
				// client forwards the request to the remote broker
				mClient.send(packageBytes);
				
				// wait for the results from the remote broker
				// the results will be used to forward back to the 
				// local broker
				byte[] results = null;
				
				return results;
			}
			
			@Override
			public void receivedTask(String clientId, int dataSize) {
				
			}
			
			@Override
			public String info() {
				// define the Worker's service description of the worker
				// under the Bridge
				String json = 
				  "{" +
					"\"code\" : \"REGISTER\"," +
					"\"id\" : \"" + mWorker.workerId + "\"," +
					"\"functions\" : []" +
				  "}";
				return json;
			}
		};
		
		// create a Client to handle communication with the remote broker
		// this Client will receive request from the bridge's Worker and
		// forward it to the remote broker 
		mClient = new Client(remoteBrokerIp) {
			@Override
			public void receive(byte[] data) {
				// forward this to the local broker
				
			}
		};
	}
	
}
