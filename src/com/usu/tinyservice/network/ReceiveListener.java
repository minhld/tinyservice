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
	 * @param data
	 */
	public void dataReceived(byte[] data);
}