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
	private int localBrokerPort = NetUtils.WORKER_PORT;
	private int remoteBrokerPort = NetUtils.CLIENT_PORT;
	
	// private String bridgeId;
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
	
	/**
	 * 
	 * @param localBrokerIp
	 * @param localWorkerPort
	 * @param remoteBrokerIp
	 * @param remoteClientPort
	 */
	public Bridge(String localBrokerIp, int localWorkerPort, 
					String remoteBrokerIp, int remoteClientPort) {
		this.localBrokerIp = localBrokerIp;
		this.remoteBrokerIp = remoteBrokerIp;
		this.start();
	}
	
	public void run() {
		// create a Client to handle communication with the remote broker
		// this Client will receive request from the bridge's Worker and
		// forward it to the remote broker 
		mClient = new Client(remoteBrokerIp, remoteBrokerPort) {
			@Override
			public void receive(String idChain, String funcName, byte[] data) {
				if (funcName.equals(NetUtils.INFO_REQUEST_SERVICES)) {
					// INFO response from the Broker 
					ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
					String funcListJson = (String) resp.outParam.values[0];
					startWorker(funcListJson);
				} else if (funcName.equals(NetUtils.INFO_WORKER_NOT_READY)) {
					
				} else {
					// other responses - worker will send it back to 
					// the previous broker
					mWorker.send(idChain, funcName, data);
				}
				
			}
		};
		NetUtils.sleep(100);
		mClient.send(NetUtils.INFO_REQUEST_SERVICES);
	}
	
	/**
	 * after the client receives the list of services available on the remote broker, 
	 * it will create a worker and delegate the service list to the worker
	 * 
	 * @param funcListJson
	 */
	void startWorker(String funcListJson) {
		// remove the '{' and '}' characters from the JSON string 
		final String subFuncList = funcListJson.substring(1, funcListJson.length() - 1);
		
		// create a Worker to handle communication with the local broker
		// this Worker does nothing but forward the requests of the local
		// broker to the bridge's Client
		mWorker = new Worker(localBrokerIp, localBrokerPort, WorkerMode.FORWARD) {
			@Override
			public void forwardRequest(String idChain, String funcName, byte[] packageBytes) {
				// client forwards the request to the remote broker
				mClient.send(idChain, funcName, packageBytes);
			}
			
			@Override
			public String info() {
				// services of this worker are simply the services from
				// the remote broker
				String json = 
					"{" +
						"\"code\" : \"REGISTER\"," +
						"\"id\" : \"" + mWorker.workerId + "\"," +
						subFuncList + 
					"}";
				return json;
			}
		};
	}
	
}
