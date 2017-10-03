package com.usu.tinyservice.classes;

import com.usu.tinyservice.network.Responder;

/**
 * this is how a generated Mobile Service class looks like 
 * creates _Server class
 * @author minhld
 *
 */
public class MobileServiceDemoXServer {
	MobileServiceDemo mobileServiceDemo;
	ResponderX resp;
	
	public MobileServiceDemoXServer() {
		mobileServiceDemo = new MobileServiceDemo();
		resp = new ResponderX();
	}
	
	class ResponderX extends Responder {
		@Override
		public void respond(byte[] req) {
			String reqStr = new String(req);
			switch (reqStr) {
				case "getRoot": {
					Object retObj = mobileServiceDemo.getRoot();
					send(retObj);
					break;
				}
			}
		}
	}
}
