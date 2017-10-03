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
		return "";
	}
	
	public String[] getFileList(String path, boolean fileOnly) {
		return null;
	}
	
	class RequesterX extends Requester {
		@Override
		public void receive(byte[] resp) {
			String respJSON = new String(resp);
			
		}
	}

}
