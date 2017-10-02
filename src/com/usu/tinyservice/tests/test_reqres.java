package com.usu.tinyservice.tests;

import com.usu.tinyservice.network.Requester;
import com.usu.tinyservice.network.Responder;

public class test_reqres extends Thread {
	public void run() {
		// start server
		ResponderEx res = new ResponderEx();
		res.start();

		RequesterEx req = new RequesterEx();
		req.start();

		while (!isInterrupted()) {
			req.send("hello".getBytes());
			try {
				Thread.sleep(1000);
			} catch (Exception e) { }
		}

		req.close();
	}
	
	class RequesterEx extends Requester {
		@Override
		public void receive(byte[] data) {
			System.out.println("client received: " + new String(data));
		}
	}
	
	class ResponderEx extends Responder {
		@Override
		public void respond(byte[] data) {
			send((new String(data) + "_returned").getBytes());
		}
		
	}
	
	public static void main(String args[]) {
		new test_reqres().start();
	}
}
