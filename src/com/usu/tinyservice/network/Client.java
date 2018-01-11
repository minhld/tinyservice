package com.usu.tinyservice.network;

import org.zeromq.ZMQ;

import com.usu.tinyservice.network.NetUtils.WorkMode;

/**
 * this Client is used to send jobs to server (broker)
 * 
 * Created by minhld on 8/18/2016.
 */
public abstract class Client extends Thread {
	
    private ZMQ.Context context;
    private ZMQ.Socket requester;
    
    private String groupIp = NetUtils.DEFAULT_IP;
    private int port = NetUtils.CLIENT_PORT;
    
    private WorkMode workMode = WorkMode.NORMAL;
    private String clientPreffix;
    public String clientId;
    
    public Client() {
        this.start();
    }

    public Client(String groupIp) {
        this.groupIp = groupIp;
        this.start();
    }

    public Client(String groupIp, int port) {
        this.groupIp = groupIp;
        this.port = port;
        this.start();
    }

    public Client(String groupIp, WorkMode workMode) {
        this.groupIp = groupIp;
        this.workMode = workMode;
        this.start();
    }
    
    public Client(String groupIp, int port, WorkMode workMode) {
        this.groupIp = groupIp;
        this.port = port;
        this.workMode = workMode;
        this.start();
    }
    
    public void run() {
        // create context and connect client to the broker/worker
        // with a pre-defined Id
        ZMQ.Context context = ZMQ.context(1);
        requester = context.socket(ZMQ.REQ);
        NetUtils.setId(requester);
        // this.clientId = new String(this.requester.getIdentity());
        requester.connect("tcp://" + this.groupIp + ":" + this.port);
        // print message
        clientPreffix = (this.workMode == WorkMode.FORWARD ? "Bridge-" : "") + "Client";
        clientId = new String(requester.getIdentity());
        NetUtils.print("[" + clientPreffix + "-" + clientId + "] Connected To " + 
        			"'" + this.groupIp + ":" + this.port + "'");
    }

	public void close() {
        requester.close();
        context.term();
	}
	
	/**
	 * send a request message for info, not for a result. This function
	 * is used when Client want to contact the Broker for special info
	 * from the Broker, such as service list, resource availability etc.
	 * 
	 * @param requestType
	 */
	public void send(String requestType) {
		send(requestType, new byte[0]);
	}
	
	/**
	 * the client sends a request including a function name and 
	 * a request package to the connected broker
	 * 
	 * @param funcName
	 * @param data
	 */
    public void send(String funcName, String data) {
		send(funcName, data.getBytes());
	}
    
    /**
     * the client sends a request including a function name and
     * a request package in binary format to the connected broker. 
     * A message includes 3 fields:
     * <br/>
     * 	- ID of the sender<br/>
     * 	- additional ID chain, in the format of {ID1}/{ID2}/.../{IDn}<br/>
     * 	- request body
     * 
     * @param funcName
     * @param data
     */
    public void send(final String funcName, final byte[] data) {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				if (requester != null) {
					// make up a sending message
					requester.sendMore(NetUtils.EMPTY);		// send from original client -> no need ID
					requester.sendMore(NetUtils.DELIMITER);
					requester.sendMore(funcName);
					requester.sendMore(NetUtils.DELIMITER);
					requester.send(data);

					// then wait for the result
					waitForResult();
				}
			}
		}).start();
	}

	/**
	 * this function forwards a request to the remote Broker. This
	 * feature is equipped for the Client to use when it becomes a
	 * module of the Bridge.
	 * A message includes 3 fields:
     * <br/>
     * 	- ID of the sender<br/>
     * 	- additional ID chain, in the format of {ID1}/{ID2}/.../{IDn}<br/>
     * 	- request body
     * 
	 * @param idChain ID chain of the requesting clients 
	 * @param funcName function name
	 * @param requestData
	 */
	public void send(String idChain, String funcName, byte[] requestData) {
		requester.sendMore(idChain);
		requester.sendMore(NetUtils.DELIMITER);
		requester.sendMore(funcName);
		requester.sendMore(NetUtils.DELIMITER);
		requester.send(requestData);

		// then wait for the result
		waitForResult();
	}

	/**
	 * WAIT FOR THE RESULT 
	 */
	private void waitForResult() {
		// and start listening for the response
		String idChain = requester.recvStr();
		
		// skip the delimiter
		requester.recv();
		
		// get the function name on the way back to ensure it receives
		// the correct result for the according function name
		String retFuncName = requester.recvStr();
		
		// skip the 2nd delimiter
		requester.recv();
		
		// inform the receive() function which defined by developer
		// to handle the result at the client
		byte[] resp = requester.recv(0);
		NetUtils.print("[" + clientPreffix + "-" + clientId + "] Received: ");
		receive(idChain, retFuncName, resp);
	}
	
    /**
     * this handler is invoked when results come back to the client 
     * 
     * @param idChain
     * @param funcName
     * @param data
     */
    public abstract void receive(String idChain, String funcName, byte[] data);
    
}
