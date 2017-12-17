package com.usu.tinyservice.network;

import org.zeromq.ZMQ;

import java.util.HashMap;

/**
 * Created by minhld on 8/4/2016.
 * This class work as a broker among the devices in mobile network.
 * The broker has several modes. To switch mode, please use the mode
 * type switcher:
 *  - Publish-Subscribe mode
 *  - Router mode
 *
 */
public class Broker extends Thread {
    private String brokerIp = NetUtils.DEFAULT_IP;

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

    public void run() {
        // this switch
        initRouterMode();
    }


    /**
     * this function is called when developer invoke router mode
     * which is for job distribution
     */
    private void initRouterMode() {
        ZMQ.Context context = ZMQ.context(1);

        // initiate publish socket
        String frontendPort = "tcp://" + this.brokerIp + ":" + NetUtils.CLIENT_PORT;
        ZMQ.Socket frontend = context.socket(ZMQ.ROUTER);
        frontend.bind(frontendPort);

        // initiate subscribe socket
        String backendPort = "tcp://" + this.brokerIp + ":" + NetUtils.SERVER_PORT;
        ZMQ.Socket backend = context.socket(ZMQ.ROUTER);
        backend.bind(backendPort);

        // Queue of available workers
        funcMap = new HashMap<String, String>();


        // String workerId = "", clientId = "";
        byte[] empty, request, reply;
        while (!Thread.currentThread().isInterrupted()) {
            ZMQ.Poller items = new ZMQ.Poller(2);
            items.register(backend, ZMQ.Poller.POLLIN);
            items.register(frontend, ZMQ.Poller.POLLIN);

            // hold until there is any messages from workers or clients
            if (items.poll() < 0)
                break;

            // HANDLE WORKER'S ACTIVITY ON BACK-END
            if (items.pollin(0)) {
                // queue worker address for LRU routing
                // FIRST FRAME is WORKER ID
                String workerId = backend.recvStr();

                // SECOND FRAME is a DELIMITER, empty
                empty = backend.recv();
                assert (empty.length == 0);

                // get THIRD FRAME
                //  - is READY (worker reports with DRL)
                //  - or CLIENT ID (worker returns results)
                String workerResponse = backend.recvStr();

                if (workerResponse.contains(NetUtils.WORKER_REGISTER)) {
                    // WORKER has finished loading, returned DRL value
                    // update worker list
                	String[] funcs = NetUtils.getFunctions(workerResponse);
                	for (int i = 0; i < funcs.length; i++) {
                		funcMap.put(funcs[i], workerId);
                	}
                	
                    System.err.println("[Broker] Add New Worker [" + workerId + "]");
                } else if (workerResponse.equals(NetUtils.WORKER_FAILED)) {
                	// get delimiter
                    backend.recv();
                    
                    System.err.println("[Broker] Worker [" + workerId + "]");
                	
                } else {
                	// WORKER SUCCESSFULLY DONE
                    // WORKER has completed the task, returned the results
                    startTime = System.currentTimeMillis();

                    // retrieve client ID - to forward the result to the client
                    String clientId = workerResponse;

                    // get FORTH FRAME, should be EMPTY - check the delimiter again
                    empty = backend.recv();
                    assert (empty.length == 0);

                    // get LAST FRAME - main result from worker
                    reply = backend.recv();

                    
                    // return the result from worker 
                    frontend.sendMore(clientId);
                    frontend.sendMore(NetUtils.DELIMITER);
                    frontend.send(reply);
                    
                    System.err.println("[Broker] Forward To Client [" + clientId + "]");
                } 
            }

            // HANDLE CLIENT'S ACTIVITIES AT FRONT-END
            if (items.pollin(1)) {
                // now get next client request, route to LRU worker
                // client request is [address][empty][request]
                String clientId = frontend.recvStr();

                // check 2nd frame
                empty = frontend.recv();
                assert (empty.length == 0);
                
                // get function name - to find worker ID
                String funcName = frontend.recvStr();
                String workerId = funcMap.get(funcName);

                // check 2nd frame
                empty = frontend.recv();
                assert (empty.length == 0);

                // get 3rd frame
                request = frontend.recv();
                
                // check if worker is available at the time of 
                // execution 
                if (workerId == null) {
                	// if worker is not available, broker will
                	// remind 
                    frontend.sendMore(clientId);
                    frontend.sendMore(NetUtils.DELIMITER);
                    
                    // create a denied message
                    byte[] deniedMsg = NetUtils.createInfoMessage(NetUtils.WORKER_NOT_READY);
                    frontend.send(deniedMsg);

                    System.err.println("[Broker] Denied Client [" + clientId + "]");
                } else {
	                // send the requests to all the nearby workers for DRL values. After receiving
	                // all DRL values, it will consider DRLs and divide job into tasks with
	                // proportional data amounts to the DRL values.
	                backend.sendMore(workerId);
	                backend.sendMore(NetUtils.DELIMITER);
	                backend.sendMore(clientId); 
	                backend.sendMore(NetUtils.DELIMITER);
	                backend.send(request);
	                
	                System.err.println("[Broker] Forward To Worker [" + workerId + "]");
                }
            }

        }

        frontend.close();
        backend.close();
        context.term();
    }


    /**
     * this class contains information about status of a worker
     * at the moment worker is requested for DRL
     */
    class WorkerInfo {
        public String workerId;
        public float DRL;

        public WorkerInfo(String workerId) {
            this.workerId = workerId;
            this.DRL = 0;
        }

        public WorkerInfo(String workerId, float drl) {
            this.workerId = workerId;
            this.DRL = drl;
        }
    }


}
