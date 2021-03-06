package com.usu.tinyservice.network;

import org.zeromq.ZMQ;

import com.usu.tinyservice.network.utils.Function;
import com.usu.tinyservice.network.utils.RegInfo;
import com.usu.tinyservice.network.utils.WorkerInfo;

import java.util.HashMap;

/**
 * Broker forwards the messages among the components in the network.
 *
 * @author minhld on 8/4/2016.
 */
public class Broker extends Thread {
    private String brokerIp = NetUtils.DEFAULT_IP;
    private int clientPort = NetUtils.CLIENT_PORT;
    private int workerPort = NetUtils.WORKER_PORT;

    private String brokerId;
    
    /**
     * this is a map of functions, each contains a list of Workers
     * that providing that function
     */
    private HashMap<String, Function> functionMap;
    // private static HashMap<String, JobMergeInfo> jobMergeList;
    
    // private static ZMQ.Socket backend;
    // private AckServerListener ackServer;

    // long startTime = 0;
    // static long startRLRequestTime = 0;

    public Broker() {
        this.start();
    }
    
    public Broker(String brokerIp) {
        this.brokerIp = brokerIp;
        this.start();
    }

    public Broker(String brokerIp, int clientPort, int workerPort) {
        this.brokerIp = brokerIp;
        this.clientPort = clientPort;
        this.workerPort = workerPort;
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
        String backendPort = "tcp://" + this.brokerIp + ":" + this.workerPort;
        ZMQ.Socket backend = context.socket(ZMQ.ROUTER);
        // backend = context.socket(ZMQ.ROUTER);
        NetUtils.setId(backend);
        backend.bind(backendPort);

        this.brokerId = new String(backend.getIdentity());
        
        NetUtils.printX("[Broker-" + this.brokerId + "] Started At " +
        			"'" + this.brokerIp + "' Client Port " + this.clientPort + " " + 
        			"Worker Port " + this.workerPort);
        
        // Queue of available workers
        functionMap = new HashMap<>();

        // INFINITE LOOP TO LISTEN TO MESSAGES FROM 
        byte[] request, reply;
        String clientId = "", idChain = "";
        long startForwardTime = 0, durForwardTime = 0;

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
                
                if (workerInfo.contains(NetUtils.WORKER_REGISTER) ||
                		workerInfo.contains(NetUtils.WORKER_FORWARD)) {
                    // WORKER has finished loading, returned DRL value
                    // update worker list
                	
                	RegInfo regInfo = NetUtils.getRegInfo(workerInfo);
                	addToFunctionMap(regInfo.functions);
                	
                	NetUtils.printX("[Broker-" + brokerId + "] Adding New Worker [" + workerId + "]");
                	NetUtils.printX("[Broker-" + brokerId + "] Added From Worker [" + workerId + "] " + services());
                	
                	// skip the last frame
                    backend.recv();
                } else if (workerInfo.equals(NetUtils.INFO_WORKER_FAILED)) {
                    String msg = "Worker [" + workerId + "] Has Problem.";
                	NetUtils.printX("[Broker-" + brokerId + "] " + msg);
                	
                    // skip the last frame
                    backend.recv();

                    // forward the error back to the Client

                    // return the result from worker
                    frontend.sendMore(clientId);
                    frontend.sendMore(NetUtils.DELIMITER);
                    frontend.sendMore(idChain);
                    frontend.sendMore(NetUtils.DELIMITER);
                    frontend.sendMore(NetUtils.BROKER_INFO);
                    frontend.sendMore(NetUtils.DELIMITER);
                    frontend.send(NetUtils.createMessage(NetUtils.INFO_WORKER_FAILED));
                } else {
                	// WORKER SUCCESSFULLY DONE
                    // WORKER has completed the task, returned the results

                    // startTime = System.currentTimeMillis();
                    // calculate time of receiving & sending only, will exclude waiting time
                    startForwardTime = System.currentTimeMillis();

                    String funcName = backend.recvStr();
                    
                    // skip the third delimiter
                    backend.recv();
                    
                    // get ID chain (index 0) & client ID (index 1) 
                    String[] idList = NetUtils.getLastClientId(workerInfo);
                    clientId = idList[0];
                    idChain = idList[1];
                    
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

                    durForwardTime += System.currentTimeMillis() - startForwardTime;

                    NetUtils.printX("[Broker-" + brokerId + "] Forward To Client [" + clientId + "] (" + durForwardTime + "ms)");
                } 
            }

            // ====== HANDLE CLIENT REQUESTS ====== 
            if (items.pollin(1)) {
                // now get next client request, route to LRU worker
                // get the ID of the sending client, where it connect to this broker 
                clientId = frontend.recvStr();

                // skip the delimiter
                frontend.recv();
                
                // get the chain of IDs of the requesting clients
                idChain = frontend.recvStr();
                
                // skip the delimiter
                frontend.recv();
                
                // get function name - to find worker IDs
                String funcName = frontend.recvStr();
                
                String workerId = "";
                if (funcName.equals(NetUtils.INFO_REQUEST_SERVICES)) {
                	workerId = NetUtils.INFO_REQUEST_SERVICES;
                } else {
                	// select the workerId from the worker list
                	WorkerInfo[] workers = getWorkerList(funcName);
                	String workerIdChain = selectWorker(workers);
                	String[] ids = NetUtils.getLastClientId(workerIdChain);
                	workerId = ids[0];
                }
                
                // skip the 2nd frame
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
                    
                    NetUtils.printX("[Broker-" + brokerId + "] Denied Client [" + clientId + "] - Function Not Found.");
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
                    
                	NetUtils.printX("[Broker-" + brokerId + "] Passed Service Info To Bridge Client [" + clientId + "]");
                	NetUtils.printX("[Broker-" + brokerId + "] " + serviceList);
                } else {
                	// WORKERS AVAILABLE

                    // get the time of receiving message
                    startForwardTime = System.currentTimeMillis();

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

	                durForwardTime = System.currentTimeMillis() - startForwardTime;

	                NetUtils.printX("[Broker-" + brokerId + "] Sent To Worker [" + workerId + "]");
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
    	Function[] funcList = functionMap.values().toArray(new Function[] {});
    	return NetUtils.createForwardMessage(brokerId, funcList);
    }
    
    private String selectWorker(WorkerInfo[] workers) {
    	return workers[0].workerId;
    }
    
    /**
     * add a function list to the map
     * 
     * @param funcList
     */
    private void addToFunctionMap(Function[] funcList) {
    	Function existFunction;
    	for (int i = 0; i < funcList.length; i++) {
    		
    		// find the existing worker list providing a function
			existFunction = functionMap.get(funcList[i].functionName);
    		if (existFunction == null) {
    			existFunction = funcList[i];
    		} else {
    			// add the workers to the existing list
    			existFunction.addWorkerInfos(funcList[i].workerInfos);
    		}
    		
    		// update the worker list
    		functionMap.put(funcList[i].functionName, existFunction);
    	}
    }
    
    /**
     * [private] get the worker list having the function name
     * 
     * @param funcName
     * @return
     */
    private WorkerInfo[] getWorkerList(String funcName) {
    	Function func = functionMap.get(funcName);
    	return func.getWorkerInfos();
    }

}
