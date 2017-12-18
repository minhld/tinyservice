package com.usu.tinyservice.network;

import org.zeromq.ZMQ;

/**
 * this Client is used to send jobs to server (broker)
 * Created by minhld on 8/18/2016.
 */
public abstract class Client extends Thread {
    private ZMQ.Context context;
    private ZMQ.Socket requester;
    // private String clientId;
    
    private String groupIp = NetUtils.DEFAULT_IP;
    private int port = NetUtils.CLIENT_PORT;
    
    public Client() {
        this.start();
    }

    public Client(String groupIp) {
        this.groupIp = groupIp;
        this.start();
    }

    public Client(String groupIp, int port) {
        this.groupIp = groupIp;
        this.port = port;
        this.start();
    }

    public void run() {
        // create context and connect client to the broker/worker
        // with a pre-defined Id
        ZMQ.Context context = ZMQ.context(1);
        requester = context.socket(ZMQ.REQ);
        NetUtils.setId(requester);
        // this.clientId = new String(this.requester.getIdentity());
        requester.connect("tcp://" + this.groupIp + ":" + this.port);
        // print message
        System.out.println("[Client-" + new String(requester.getIdentity()) + "] Started.");
    }

	public void close() {
        requester.close();
        context.term();
	}
	
	/**
	 * the client forwards the request to the remote broker. This
	 * feature is used when client is a part of the Bridge
	 * 
	 * @param requestData
	 */
	public void send(byte[] requestData) {
		requester.send(requestData);
	}

	/**
	 * the client sends a request including a function name and 
	 * a request package to the connected broker
	 * 
	 * @param funcName
	 * @param data
	 */
    public void send(String funcName, String data) {
		send(funcName, data.getBytes());
	}
    
    /**
     * the client sends a request including a function name and
     * a request package in binary format to the connected broker
     * 
     * @param funcName
     * @param data
     */
    public void send(String funcName, byte[] data) {
		if (requester != null) {
			requester.sendMore(funcName);
			requester.sendMore(NetUtils.DELIMITER);
			requester.send(data);
			byte[] resp = requester.recv(0);
			receive(resp);
		}
	}
    
    /**
     * this handler is invoked when results come back to the client 
     * 
     * @param data
     */
    public abstract void receive(byte[] data);
    
}
