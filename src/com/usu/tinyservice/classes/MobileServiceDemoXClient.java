package com.usu.tinyservice.classes;

import com.usu.tinyservice.network.Requester;

/**
 * this is how a generated Mobile Service class looks like 
 * create _Client class
 * 
 * @author minhld
 *
 */
public class MobileServiceDemoXClient {
	RequesterX req;
	
	public MobileServiceDemoXClient() {
		req = new RequesterX();
		
	}
	
	public String getRoot() {
		
	}
	
	class RequesterX extends Requester {
		@Override
		public void receive(byte[] data) {
			
		}
	}

}
