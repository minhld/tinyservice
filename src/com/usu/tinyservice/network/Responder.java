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
		this.serverIp = Constants.SERVER_GENERAL_IP;
		this.port = Constants.REQUEST_PORT;
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
	        	byte[] req = responder.recv(0);
	        	respond(req);
	        }
	        
	        responder.close();
	        context.term();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void send(Object data) {
		byte[] byteData = BinaryHelper.object2ByteArray(data);
		responder.send(byteData);
	}
	
	public void send(byte[] data) {
		responder.send(data);
	}
	
	public abstract void respond(byte[] req);
	
}
