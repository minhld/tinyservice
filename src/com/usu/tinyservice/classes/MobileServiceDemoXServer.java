package com.usu.tinyservice.classes;

import com.usu.tinyservice.network.Responder;

/**
 * this is how a generated Mobile Service class looks like 
 * creates _Server class
 * @author minhld
 *
 */
public class MobileServiceDemoXServer {
	ResponderX responder;
	
	public MobileServiceDemoXServer() {
		responder = new ResponderX();
	}
	
	public String getRoot() {
		
	}
	
	class ResponderX extends Responder {
		@Override
		public void respond(byte[] data) {
			
		}
	}
}
