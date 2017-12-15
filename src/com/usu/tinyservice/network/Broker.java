package com.usu.tinyservice.network;

import org.zeromq.ZMQ;

import com.usu.tinyservice.messages.binary.RequestMessage;

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
                String workerDataJson = backend.recvStr();

                if (workerDataJson.contains(NetUtils.WORKER_REGISTER)) {
                    // WORKER has finished loading, returned DRL value
                    // update worker list
                	String[] funcs = NetUtils.getFunctions(workerDataJson);
                	for (int i = 0; i < funcs.length; i++) {
                		funcMap.put(funcs[i], workerId);
                	}
                	
                    System.err.println("[Broker] Add New Worker [" + workerId + "]");

                } else {
                    // WORKER has completed the task, returned the results
                    startTime = System.currentTimeMillis();

                    // get FORTH FRAME, should be EMPTY - check the delimiter again
                    empty = backend.recv();
                    assert (empty.length == 0);

                    // get LAST FRAME - main result from worker
                    reply = backend.recv();

                    String clientId = "";
                    
                    // return the result from worker 
                    frontend.sendMore(clientId);
                    frontend.sendMore(NetUtils.BROKER_DELIMITER);
                    frontend.send(reply);
                    
                    System.err.println("[Broker] Send Response To Client [" + clientId + "]");
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
                
                // send the requests to all the nearby workers for DRL values. After receiving
                // all DRL values, it will consider DRLs and divide job into tasks with
                // proportional data amounts to the DRL values.
                backend.sendMore(workerId);
                backend.sendMore(NetUtils.BROKER_DELIMITER);
                backend.sendMore(clientId); 
                backend.sendMore(NetUtils.BROKER_DELIMITER);
                backend.send(request);
                
                System.err.println("[Broker] Send Request To Worker [" + workerId + "]");
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
