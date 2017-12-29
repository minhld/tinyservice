package com.usu.tinyservice.network;

import org.zeromq.ZMQ;

/**
 * The worker serves as a servant for the broker. It receives tasks
 * from the broker and alternatively executes those tasks. The worker
 * only work with Broker running in Router mode
 *
 * Created by minhld on 8/18/2016.
 */
public abstract class Worker extends Thread {
	public static enum WorkerMode {
		NORMAL,
		FORWARD
	}

	private ZMQ.Socket worker;
    // private ExAckClient ackClient;
	
	// network and type information
    private String groupIp = "*";
    private int port = NetUtils.WORKER_PORT;
    
    // worker will operate in two modes NORMAL
    private WorkerMode mode = WorkerMode.NORMAL;
    private String workerPrefix = "";
    
    public String workerId = "";

    public Worker() {
        this.start();
    }

    public Worker(String groupIp) {
        this.groupIp = groupIp;
        this.start();
    }

    public Worker(String groupIp, int port) {
        this.groupIp = groupIp;
        this.port = port;
        this.start();
    }

    public Worker(String groupIp, WorkerMode workerMode) {
        this.groupIp = groupIp;
        this.mode = workerMode;
        this.start();
    }

    public Worker(String groupIp, int port, WorkerMode workerMode) {
        this.groupIp = groupIp;
        this.port = port;
        this.mode = workerMode;
        this.start();
    }
    
    public void run() {
        initWithBroker();
    }

    /**
     * initiate worker with broker at the middle
     */
    private void initWithBroker() {
        try {
            ZMQ.Context context = ZMQ.context(1);

            // socket to talk to clients and set its Id
            worker = context.socket(ZMQ.REQ);
            NetUtils.setId (worker);
            this.workerId = new String(worker.getIdentity());
            worker.connect("tcp://" + this.groupIp + ":" + this.port);

            // to report worker has finished the initialization
            // workerStarted(this.workerId);
            workerPrefix = (this.mode == WorkerMode.FORWARD ? "Forward" : "") + "-Worker";
            NetUtils.print("[" + workerPrefix + "-" + workerId + "] Connected At " +
            			"'" + this.groupIp + ":" + this.port + "'.");

            // inform broker that i am ready
            // worker.send(NetUtils.WORKER_READY);
            String registerInfo = info();
            send(registerInfo);
            
            // // initiate ACK client - to listen to DRL request from brokers
            // ackClient = new ExAckClient(context, this.groupIp, worker.getIdentity());

            // this part is to wait for broker to send job to execute
            // String clientId;
            byte[] request, result;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // get client ID chain
                    String idChain = worker.recvStr();

                    // set start job clock
                    long startTime = System.currentTimeMillis();

                    // skip delimiter
                    worker.recv();
                    
                    String funcName = worker.recvStr();
                    
                    // skip the second delimiter
                    worker.recv();
                    
                    // get request and resolve the request
                    request = worker.recv();
                    
                    if (mode == WorkerMode.NORMAL) {
                    	// resolve the request by parsing the request and perform
                    	// something on the data to retrieve result
                    	result = resolveRequest(request);
                    	
                        // and return result back to the broker
                        send(idChain, funcName, result);
                    } else if (mode == WorkerMode.FORWARD) {
                    	// request will be forwarded to somewhere else -  
                    	// navigated by developer's code
                    	forwardRequest(idChain, funcName, request);
                    	// forwardRequest(request);
                    }
                    
                    // end the job execution clock
                    TaskDone taskInfo = new TaskDone();
                    taskInfo.durration = System.currentTimeMillis() - startTime;
                    
                    // when worker completes the task
                    // workerFinished(workerId, taskInfo);
                    NetUtils.print("[" + workerPrefix + "-" + workerId + "] Completed In " + taskInfo.durration + "m\n");

                } catch (Exception d) {
                    d.printStackTrace();
                    send(NetUtils.INFO_WORKER_FAILED);
                }
            }
            worker.close();
            context.term();
        } catch (Exception e) {
            // exception there - leave it for now
            e.printStackTrace();
        }
    }
    
    public void send(String info) {
    	worker.sendMore(info);
        worker.sendMore(NetUtils.DELIMITER);
        worker.send(NetUtils.EMPTY);
    }
    
//    /**
//     * forwards the result back to the broker. A response includes: <br/>
//     * 	- worker ID<br/>
//     * 	- ID chain<br/>
//     * 	- response
//     * 
//     * @param idChain format {ID1}/{ID2}/... {@link Client}
//     * @param data String format
//     */
//    public void send(String idChain, String funcName, String data) {
//    	worker.sendMore(idChain);
//        worker.sendMore(NetUtils.DELIMITER);
//        worker.sendMore(funcName);
//        worker.sendMore(NetUtils.DELIMITER);
//        worker.send(data);
//    }
    
    /**
     * forwards the result back to the broker. A response includes: <br/>
     * 	- worker ID<br/>
     * 	- ID chain<br/>
     * 	- response 
     * 
     * @param idChain format {ID1}/{ID2}/... {@link Client}
     * @param data binary format
     */
    public void send(String idChain, String funcName, byte[] data) {
    	worker.sendMore(idChain);
        worker.sendMore(NetUtils.DELIMITER);
        worker.sendMore(funcName);
        worker.sendMore(NetUtils.DELIMITER);
        worker.send(data);
    }

    
//    class ExAckClient extends AckClient {
//        public ExAckClient(ZMQ.Context context, String ip, byte[] id) {
//            super(context, ip, id);
//        }
//
//        @Override
//        public void sendResponse(String topic, byte[] request) {
//            // this delegate function is called when client detects a DRL request
//            // from server and try responding to it with DRL info
//
//            // this is the place to send back device info
//            String reqStr = new String(request);
//            if (reqStr.equals("ack_request")) {
//                // receive device resource information, and calculate DRL value here
//                String drl = "";
//                this.sendMessage(drl.getBytes());
//            }
//        }
//    }


    /**
     * this abstract function needs to be filled. this is to
     * define how worker will complete the work.
     * <br/><br/>
     * default code is implemented
     *
     * @param packageBytes
     * @return
     */
    public byte[] resolveRequest(byte[] packageBytes) {
    	return null;
    }

    /**
     * this function will be used by the Bridge. This forwards request message
     * to another broker - implemented by developer's injected code
     * <br/><br/>
     * default code is implemented
     * 
     * @param idChain
     * @param funcName
     * @param packageBytes
     */
    public void forwardRequest(String idChain, String funcName, byte[] packageBytes) { }

    /**
     * this holds information of the current Worker. The information may 
     * include code (REGISTER/FORWARD), worker ID and function list that
     * worker provides.
     * <br/><br/>
     * please see the example for the detail.
     * 
     * @return worker information in JSON format
     */
    public abstract String info();
    
    /**
     * this class contains information of the task of which has just been
     * executed by the worker
     */
    public class TaskDone {
        public long durration;

    }
}
