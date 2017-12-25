package com.usu.tinyservice.network;

/**
 * this is used when the SyncMode is asynchronous mode
 * this interface will push data up to the main thread
 * instead of the receive() callback function
 * 
 * @author minhld
 *
 */
public interface ReceiveListener {
	/**
	 * 
	 * @param idChain
	 * @param funcName
	 * @param data
	 */
	public void dataReceived(String idChain, String funcName, byte[] data);
}