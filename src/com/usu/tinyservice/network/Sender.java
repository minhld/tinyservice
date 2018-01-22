package com.usu.tinyservice.network;

import org.zeromq.ZMQ;

/**
 * 
 * @author minhld
 */
public abstract class Sender extends Thread {
	ZMQ.Context context;
	private ZMQ.Socket requester;
	private String serverIp;
	private int port;
	
	public Sender() {
		this.serverIp = Constants.SERVER_GENERAL_IP;
		this.port = Constants.REQUEST_PORT;
	}
	
	public Sender(String serverIp, int port) {
		this.serverIp = serverIp;
		this.port = port;
	}
	
	public void run() {
		// 
        context = ZMQ.context(1);
        requester = context.socket(ZMQ.REQ);
        requester.bind("tcp://" + this.serverIp + ":" + this.port);
	}
	
	public void close() {
        requester.close();
        context.term();
	}
	
	public void send(String data) {
		send(data.getBytes());
	}
		
	public byte[] send(byte[] data) {
		if (requester != null) {
			requester.send(data);
			byte[] resp = requester.recv(0);
			return resp;
		}
		return null;
	}
	
	public abstract void receive(byte[] data);
	
}
