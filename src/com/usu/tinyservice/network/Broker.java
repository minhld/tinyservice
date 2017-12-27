package com.usu.tinyservice.network;

import org.zeromq.ZMQ;

import java.util.HashMap;

/**
 * Broker navigates the message flow of the system
 *
 * @author minhld on 8/4/2016.
 */
public class Broker extends Thread {
    private String brokerIp = NetUtils.DEFAULT_IP;
    private int clientPort = NetUtils.CLIENT_PORT;
    private int serverPort = NetUtils.SERVER_PORT;
    
    private HashMap<String, String> funcMap;
    // private static HashMap<String, JobMergeInfo> jobMergeList;

    // private static ZMQ.Socket backend;
    // private AckServerListener ackServer;

    long startTime = 0;
    static long startRLRequestTime = 0;

    public Broker() {
        this.start();
    }
    
    public Broker(String brokerIp) {
        this.brokerIp = brokerIp;
        this.start();
    }

    public Broker(String brokerIp, int clientPort, int serverPort) {
        this.brokerIp = brokerIp;
        this.clientPort = clientPort;
        this.serverPort = serverPort;
        this.start();
    }
    
    public void run() {
        // this switch
        initRouterMode();
    }

    /**
     * this function is called when developer invoke router mode
     * which is for job distribution
     */
    void initRouterMode() {
        ZMQ.Context context = ZMQ.context(1);

        // initiate publish socket
        String frontendPort = "tcp://" + this.brokerIp + ":" + this.clientPort;
        ZMQ.Socket frontend = context.socket(ZMQ.ROUTER);
        frontend.bind(frontendPort);

        // initiate subscribe socket
        String backendPort = "tcp://" + this.brokerIp + ":" + this.serverPort;
        ZMQ.Socket backend = context.socket(ZMQ.ROUTER);
        // backend = context.socket(ZMQ.ROUTER);
        backend.bind(backendPort);

        // Queue of available workers
        funcMap = new HashMap<String, String>();


        // INFINITE LOOP TO LISTEN TO MESSAGES FROM 
        byte[] request, reply;
        while (!Thread.currentThread().isInterrupted()) {
            ZMQ.Poller items = new ZMQ.Poller(2);
            items.register(backend, ZMQ.Poller.POLLIN);
            items.register(frontend, ZMQ.Poller.POLLIN);

            // hold until there is any messages from workers or clients
            if (items.poll() < 0)
                break;

            // ====== HANDLE WORKER'S ACTIVITY ON BACK-END ====== 
            if (items.pollin(0)) {
                // queue worker address for LRU routing
                // FIRST FRAME is WORKER ID
                String workerId = backend.recvStr();

                // skip the delimiter
                backend.recv();
                
                // get THIRD FRAME
                //  - is READY (worker reports with DRL)
                //  - or CLIENT ID (worker returns results)
                String workerInfo = backend.recvStr();

                // skip the second delimiter
                backend.recv();
                
                if (workerInfo.contains(NetUtils.WORKER_REGISTER)) {
                    // WORKER has finished loading, returned DRL value
                    // update worker list
                	String[] funcs = NetUtils.getFunctions(workerInfo);
                	for (int i = 0; i < funcs.length; i++) {
                		funcMap.put(funcs[i], workerId);
                	}
                	NetUtils.printX("[Broker] Add New Worker [" + workerId + "]");

                	// skip the last frame
                    backend.recv();
                } else if (workerInfo.equals(NetUtils.INFO_WORKER_FAILED)) {
                	NetUtils.printX("[Broker] Worker [" + workerId + "] Has Problem.");
                	
                    // skip the last frame
                    backend.recv();
                } else {
                	// WORKER SUCCESSFULLY DONE
                    // WORKER has completed the task, returned the results
                    startTime = System.currentTimeMillis();

                    String funcName = backend.recvStr();
                    
                    // skip the third delimiter
                    backend.recv();
                    
                    // get ID chain (index 0) & client ID (index 1) 
                    String[] idList = NetUtils.getLastClientId(workerInfo);
                    String clientId = idList[0];
                    String idChain = idList[1];
                    
                    // get LAST FRAME - main result from worker
                    reply = backend.recv();

                    // return the result from worker 
                    frontend.sendMore(clientId);
                    frontend.sendMore(NetUtils.DELIMITER);
                    frontend.sendMore(idChain);
                    frontend.sendMore(NetUtils.DELIMITER);
                    frontend.sendMore(funcName);
                    frontend.sendMore(NetUtils.DELIMITER);
                    frontend.send(reply);
                    
                    NetUtils.printX("[Broker] Forward To Client [" + clientId + "]");
                } 
            }

            // ====== HANDLE CLIENT REQUESTS ====== 
            if (items.pollin(1)) {
                // now get next client request, route to LRU worker
                // get the ID of the sending client, where it connect to this broker 
                String clientId = frontend.recvStr();

                // skip the delimiter
                frontend.recv();
                
                // get the chain of IDs of the requesting clients
                String idChain = frontend.recvStr();
                
                // skip the delimiter
                frontend.recv();
                
                // get function name - to find worker ID
                String funcName = frontend.recvStr();
                String workerId = funcName.equals(NetUtils.INFO_REQUEST_SERVICES) ? funcName : funcMap.get(funcName);

                // // check 2nd frame
                // empty = frontend.recv();
                // assert (empty.length == 0);
                frontend.recv();

                // get 3rd frame
                request = frontend.recv();
                
                // ====== CHECK AVAILABILITY OF THE WORKER ======  
                
                // check if worker is available at the time of execution 
                if (workerId == null) {
                	// WORKER NOT AVAILABLE
                	
                    // send back a denied message to the requesting client
                    byte[] deniedMsgBytes = NetUtils.createMessage(NetUtils.INFO_WORKER_NOT_READY);
                    frontend.sendMore(clientId); 
                    frontend.sendMore(NetUtils.DELIMITER);
                    frontend.sendMore(idChain);
                    frontend.sendMore(NetUtils.DELIMITER);
                    frontend.sendMore(NetUtils.INFO_WORKER_NOT_READY);
                    frontend.sendMore(NetUtils.DELIMITER);
                    frontend.send(deniedMsgBytes);
                    // sendMsg(clientId, deniedMsgBytes);
                    
                    NetUtils.printX("[Broker] Denied Client [" + clientId + "]");
                } else if (workerId.equals(NetUtils.INFO_REQUEST_SERVICES)) {
                	// REQUEST BROKER'S SERVICE LIST
                	
                	String serviceList = services();
                	byte[] serviceListBytes = NetUtils.createMessage(serviceList);
                	frontend.sendMore(clientId); 
                	frontend.sendMore(NetUtils.DELIMITER);
                	frontend.sendMore(idChain);
                	frontend.sendMore(NetUtils.DELIMITER);
                	frontend.sendMore(NetUtils.INFO_REQUEST_SERVICES);
                	frontend.sendMore(NetUtils.DELIMITER);
                	frontend.send(serviceListBytes);
                	// sendMsg(clientId, serviceListBytes);
                    
                	NetUtils.printX("[Broker] Send Services To Client [" + clientId + "]");
                } else {
                	// WORKER AVAILABLE
                	
                	// update the ID chain with the new client ID 
                	idChain = NetUtils.concatIds(idChain, clientId);
                	
	                // send the requests to all the nearby workers for DRL values. After receiving
	                // all DRL values, it will consider DRLs and divide job into tasks with
	                // proportional data amounts to the DRL values.
	                backend.sendMore(workerId);
	                backend.sendMore(NetUtils.DELIMITER);
	                backend.sendMore(idChain);
	                backend.sendMore(NetUtils.DELIMITER);
	                backend.sendMore(funcName);
	                backend.sendMore(NetUtils.DELIMITER);
	                backend.send(request);
	                
	                NetUtils.printX("[Broker] Forward To Worker [" + workerId + "]");
                }
            }

        }

        // terminate all components when done
        frontend.close();
        backend.close();
        context.term();
    }

    /**
     * returns the list of available services on current Broker
     * 
     * @return list of available services in string array
     */
    public String services() {
    	String functionList = 
    	  "{" + 
    	    "\"functions\" : [";
    	
    	// get the list of functions
    	String functions = "";
    	for (String key : funcMap.keySet()) {
    		functions += "{\"functionName\" : \"" + key + "\"},";
    	}
    	
    	// remove the last redundant comma, if any
    	if (functions.length() > 0) {
    		functions = functions.substring(0, functions.length() - 1);
    	}
    	
    	// continue with the suffix
    	functionList += functions +
    	    "]" + 
    	  "}";
    	return functionList;
    }

//    /**
//     * send a message to client or worker. This will send a message 
//     * including an ID and data
//     * 
//     * @param id
//     * @param data
//     */
//    void sendMsg(String id, byte[] data) {
//        backend.sendMore(id); 
//        backend.sendMore(NetUtils.DELIMITER);
//        backend.send(data);
//    }
    
//    /**
//     * this class contains information about status of a worker
//     * at the moment worker is requested for DRL
//     */
//    class WorkerInfo {
//        public String workerId;
//        public float DRL;
//
//        public WorkerInfo(String workerId) {
//            this.workerId = workerId;
//            this.DRL = 0;
//        }
//
//        public WorkerInfo(String workerId, float drl) {
//            this.workerId = workerId;
//            this.DRL = drl;
//        }
//    }

}
