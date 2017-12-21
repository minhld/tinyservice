package com.usu.tinyservice.network;

import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.Worker.WorkerMode;

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
	
	private String bridgeId;
	private Worker mWorker;
	private Client mClient;
	
	public Bridge(String remoteBrokerIp) {
		this.localBrokerIp = NetUtils.DEFAULT_IP;
		this.remoteBrokerIp = remoteBrokerIp;
		this.start();
	}
	
	public Bridge(String localBrokerIp, String remoteBrokerIp) {
		this.localBrokerIp = localBrokerIp;
		this.remoteBrokerIp = remoteBrokerIp;
		this.start();
	}
	
	public void run() {
		// create a Client to handle communication with the remote broker
		// this Client will receive request from the bridge's Worker and
		// forward it to the remote broker 
		mClient = new Client(remoteBrokerIp) {
			@Override
			public void receive(byte[] data) {
				// forward this to the local broker
				ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
				
				// create a Worker to handle communication with the local broker
				// this Worker does nothing but forward the requests of the local
				// broker to the bridge's Client
				mWorker = new Worker(localBrokerIp, WorkerMode.FORWARD) {
					@Override
					public void forwardRequest(String clientId, byte[] packageBytes) {
						// client forwards the request to the remote broker
						mClient.forward(clientId, packageBytes);
					}
					
					@Override
					public String info() {
						bridgeId = NetUtils.generateId();
								
						// define the Worker's service description of the worker
						// under the Bridge
						String json = 
						  "{" +
							"\"code\" : \"REGISTER\"," +
							"\"id\" : \"" + mWorker.workerId + "\"," +
							"\"functions\" : [\"bridge-\"" + bridgeId + "]" +
						  "}";
						return json;
					}
				};
				String clientId = "";
				mWorker.send(clientId, data);
				
				
			}
		};
		mClient.send(NetUtils.REQUEST_SERVICES);
	}
	
}
