package com.usu.tinyservice.network.utils;

import com.usu.tinyservice.network.Bridge;

/**
 * <b>DBridge</b> - Double Bridge is the bridge that gives 
 * bi-directional communication between the two Broker
 * 
 * @author Minh Le
 * @since 08/25/2018
 *
 */
public class DBridge {
	public DBridge(String localBrokerIp, int localClientPort, int localWorkerPort,
			String remoteBrokerIp, int remoteClientPort, int remoteWorkerPort) {
		
		// bridge to connect to the left broker
		new Bridge(localBrokerIp, localWorkerPort, remoteBrokerIp, remoteClientPort);
		
		// bridge to connect to the right broker
		new Bridge(remoteBrokerIp, remoteWorkerPort, localBrokerIp, localClientPort);
	}
	
}
