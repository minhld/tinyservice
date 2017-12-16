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

    }

	public void close() {
        requester.close();
        context.term();
	}

    public void send(String funcName, String data) {
		send(funcName, data.getBytes());
	}
    
    public void send(String funcName, byte[] data) {
		if (requester != null) {
			requester.sendMore(funcName);
			requester.sendMore(NetUtils.BROKER_DELIMITER);
			requester.send(data);
			byte[] resp = requester.recv(0);
			receive(resp);
		}
	}
    
    public abstract void receive(byte[] data);
    
//    protected void sendMessage(byte[] msg) {
//        requester.send(msg);
//    }
//
//    protected void sendMessage(String msg) {
//        requester.send(msg);
//    }
//
//    /**
//     * this event occurs when client finished starting
//     * @param clientId
//     */
//    public abstract void clientStarted(String clientId);
//
//    /**
//     * this function defines what task to send to the broker/worker
//     */
//    public abstract void send();
//
//    /**
//     * this function is invoked when client receives result of the
//     * task it requested. this function must be overrode to define
//     * how to manipulate with the output.
//     *
//     * @param result
//     */
//    public abstract void resolveResult(byte[] result);
}
