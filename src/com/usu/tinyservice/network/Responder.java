package com.usu.tinyservice.network;

import org.zeromq.ZMQ;

/**
 * 
 * @author minhld
 */
public abstract class Responder extends Thread {
	private ZMQ.Socket responder;
	private String serverIp;
	private int port;

	public Responder() {
		this.serverIp = Utils.SERVER_GENERAL_IP;
		this.port = Utils.REQUEST_PORT;
	}
	
	public Responder(String serverIp, int port) {
		this.serverIp = serverIp;
		this.port = port;
	}
	
	public void run() {
		try {
			// start 
	        ZMQ.Context context = ZMQ.context(1);
	        responder = context.socket(ZMQ.REP);
	        responder.connect("tcp://" + this.serverIp + ":" + this.port);

	        while (!isInterrupted()) {
	        	byte[] resp = responder.recv(0);
	        	respond(resp);
	        }
	        
	        responder.close();
	        context.term();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void send(byte[] data) {
		if (responder != null) {
			responder.send(data);
		}
	}
	
	public abstract void respond(byte[] data);
	
}
