package com.usu.tinyservice.network;

import org.zeromq.ZMQ;

/**
 * this Client is used to send jobs to server (broker)
 * Created by minhld on 8/18/2016.
 */
public abstract class Client extends Thread {
    private ZMQ.Context context;
    private ZMQ.Socket requester;
    private String clientId;
    
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
        this.clientId = new String(this.requester.getIdentity());
        requester.connect("tcp://" + this.groupIp + ":" + this.port);
        // print message
        System.out.println("[Client-" + new String(requester.getIdentity()) + "] Started.");
    }

	public void close() {
        requester.close();
        context.term();
	}
	
	/**
	 * send a request message for info, not for a result. This function
	 * is used when Client want to contact the Broker for special info
	 * from the Broker, such as service list, resource availability etc.
	 * 
	 * @param requestType
	 */
	public void send(String requestType) {
		send(requestType, new byte[0]);
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
			requester.sendMore(clientId);
			requester.sendMore(NetUtils.DELIMITER);
			requester.sendMore(funcName);
			requester.sendMore(NetUtils.DELIMITER);
			requester.send(data);
			byte[] resp = requester.recv(0);
			receive(resp);
		}
	}

	/**
	 * this function forwards a request to the remote Broker. This
	 * feature is equipped for the Client to use when it becomes a
	 * module of the Bridge.
	 * 
	 * @param clientId ID of the requesting client 
	 * @param funcName function name
	 * @param requestData
	 */
	public void send(String clientId, String funcName, byte[] requestData) {
		requester.sendMore(clientId);
		requester.sendMore(NetUtils.DELIMITER);
		requester.sendMore(funcName);
		requester.sendMore(NetUtils.DELIMITER);
		requester.send(requestData);
	}

    /**
     * this handler is invoked when results come back to the client 
     * 
     * @param data
     */
    public abstract void receive(byte[] data);
    
}
