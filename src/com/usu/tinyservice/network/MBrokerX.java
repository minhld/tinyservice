package com.usu.tinyservice.network;

import com.usu.tinyservice.messages.binary.InParam;
import com.usu.tinyservice.messages.binary.RequestMessage;
import com.usu.tinyservice.network.parsers.ByteDataParser;
import com.usu.tinyservice.network.parsers.IDataParser;
import com.usu.tinyservice.network.utils.Function;
import com.usu.tinyservice.network.utils.RegInfo;
import com.usu.tinyservice.network.utils.WorkerInfo;
import com.usu.tinyservice.network.utils.WorkerScheduler;
import org.zeromq.ZMQ;

import java.util.HashMap;

/**
 * MBroker stands for Multi-Broker which will split jobs into parts
 * and forward to the Workers 
 *
 * @author minhld on 9/2/2018.
 */
public class MBrokerX extends Thread {
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

    /**
     * temporary variable to hold a data parser for all jobs.
     * in the future, the data parser will be given by the
     * function call
     */
    private IDataParser dataParser;

    /**
     * performance window holds the performance capture of all
     * Workers,
     */
    private WorkerScheduler scheduler;

    // private static ZMQ.Socket backend;
    // private AckServerListener ackServer;

    // long startTime = 0;
    // static long startRLRequestTime = 0;

    int taskIndex = 0;
    int taskNumber = NetUtils.TASK_TOTAL_NUMBER;
    int actualTaskNumber = 0;
    int receiveTaskIndex = 0;
    byte[] placeHolder;


    public MBrokerX() {
        this.start();
    }

    public MBrokerX(String brokerIp) {
        this.brokerIp = brokerIp;
        this.start();
    }

    public MBrokerX(String brokerIp, int clientPort, int workerPort) {
        this.brokerIp = brokerIp;
        this.clientPort = clientPort;
        this.workerPort = workerPort;
        this.start();
    }

    public void run() {
        // create data parser
        dataParser = new ByteDataParser();

        // create a performance window
        scheduler = new WorkerScheduler();

        // switch to router mode
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
        			"Worker Port " + this.workerPort, NetUtils.TextColor.CYAN);

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

                	// NetUtils.printX("[Broker-" + brokerId + "] Adding New Worker [" + workerId + "]");
                	NetUtils.printX("[Broker-" + brokerId + "] Added From Worker [" + workerId + "] " + services(),
                                    NetUtils.TextColor.CYAN);

                	// skip the last frame
                    backend.recv();
                } else if (workerInfo.equals(NetUtils.INFO_WORKER_FAILED)) {
                    String msg = "Worker [" + workerId + "] Has Problem.";
                	NetUtils.printX("[Broker-" + brokerId + "] " + msg, NetUtils.TextColor.CYAN);

                    // skip the last frame
                    backend.recv();

                    // forward the error back to the Client

                    // return the result from worker
                    sendToPeer(frontend, clientId, idChain, NetUtils.BROKER_INFO,
                            NetUtils.createMessage(NetUtils.INFO_WORKER_FAILED));

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

                    // merge the reply
                    dataParser.copyPartToHolder(placeHolder, reply, receiveTaskIndex, taskNumber);

                    receiveTaskIndex++;

                    NetUtils.printX("[Broker-" + brokerId + "] Forward From Worker [" + workerId +
                                    "] To Client [" + clientId + "] (" + durForwardTime + "ms)", NetUtils.TextColor.CYAN);

                    if (receiveTaskIndex == actualTaskNumber) {
                        // report that session is over
                        scheduler.endSession();

                        // return the result from worker
                        sendToPeer(frontend, clientId, idChain, funcName, reply);

                        // calculate time for forwarding messages
                        durForwardTime += System.currentTimeMillis() - startForwardTime;

                        // reset receive task index
                        receiveTaskIndex = 0;
                    }
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

                // find info
                String infoId = null;
                WorkerInfo[] workers = null;
                if (funcName.equals(NetUtils.INFO_REQUEST_SERVICES)) {
                	infoId = NetUtils.INFO_REQUEST_SERVICES;
                } else {
                	// select the workerId from the worker list
                	workers = getWorkerList(funcName);
                	infoId = workers.length > 0 ? NetUtils.INFO_WORKERS_READY : null;

                	// String workerIdChain = selectWorker(workers);
                	// String[] ids = NetUtils.getLastClientId(workerIdChain);
                	// workerId = ids[0];
                }

                // skip the 2nd frame
                frontend.recv();

                // get 3rd frame
                request = frontend.recv();

                // ====== CHECK AVAILABILITY OF THE WORKER ======

                // check if worker is available at the time of execution
                if (infoId == null) {
                	// WORKER NOT AVAILABLE

                    // send back a denied message to the requesting client
                    byte[] deniedMsgBytes = NetUtils.createMessage(NetUtils.INFO_WORKER_NOT_READY);

                    // send back to the client who sent the request
                    sendToPeer(frontend, clientId, idChain, NetUtils.INFO_WORKER_NOT_READY, deniedMsgBytes);

                    NetUtils.printX("[Broker-" + brokerId + "] Denied Client [" + clientId + "] - Function Not Found.");
                } else if (infoId.equals(NetUtils.INFO_REQUEST_SERVICES)) {
                	// REQUEST FOR BROKER'S SERVICE LIST

                	String serviceList = services();
                	byte[] serviceListBytes = NetUtils.createMessage(serviceList);

                	// send to the next Bridge
                    sendToPeer(frontend, clientId, idChain, NetUtils.INFO_REQUEST_SERVICES, serviceListBytes);

                	NetUtils.printX("[Broker-" + brokerId + "] Passed Service Info To Bridge Client [" + clientId + "]",
                                    NetUtils.TextColor.CYAN);
                	NetUtils.printX("[Broker-" + brokerId + "] " + serviceList, NetUtils.TextColor.CYAN);
                } else {
                	// WORKERS AVAILABLE

                    // get the time of receiving message
                    startForwardTime = System.currentTimeMillis();

                    // update the ID chain with the new client ID
                    idChain = NetUtils.concatIds(idChain, clientId);

                    // extract to get the request message object
                	RequestMessage reqMsg = (RequestMessage) NetUtils.deserialize(request);

                	if (reqMsg.requestType == RequestMessage.RequestType.ORIGINAL) {
                        // start a new session
                        String sessionId = scheduler.startSession();

                        taskIndex = 0;
                        actualTaskNumber = 0;

                        // for (WorkerInfo workerInfo : workers) {
                        for (int i = 0; i < workers.length; i++) {
                            WorkerInfo workerInfo = workers[i];
                            if (i == 0) continue;

                            String[] workerIds = NetUtils.getLastClientId(workerInfo.workerId);
                            String fwdWorkerId = workerIds[0];

                            // get divided job (sub-task) message
                            divideRequest(sessionId, reqMsg, backend, workerInfo.workerId, fwdWorkerId, idChain);

                            NetUtils.printX("[Broker-" + brokerId + "] Sent To Worker [" + fwdWorkerId + "]",
                                            NetUtils.TextColor.CYAN);
                        }
                        durForwardTime = System.currentTimeMillis() - startForwardTime;

                    } else if (reqMsg.requestType == RequestMessage.RequestType.FORWARDING) {
                        // just send a single request to worker
                        for (WorkerInfo workerInfo : workers) {
                            if (workerInfo.workerId.equals(reqMsg.endWorkerId)) {
                                String[] workerIds = NetUtils.getLastClientId(workerInfo.workerId);
                                String fwdWorkerId = workerIds[0];
                                reqMsg.endWorkerId = workerIds[1];
                                byte[] forwardRequest = NetUtils.serialize(reqMsg);
                                sendToPeer(backend, fwdWorkerId, idChain, funcName, forwardRequest);
                                NetUtils.printX("[Broker-" + brokerId + "] Sent To Worker [" + fwdWorkerId + "]",
                                        NetUtils.TextColor.CYAN);
                            }
                        }

                        durForwardTime = System.currentTimeMillis() - startForwardTime;
                    }

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

    /**
     * send a message to a peer (Client or Worker)
     *
     * @param peer socket to send
     * @param peerId ID of socket
     * @param idChain chain of IDs of the peers
     * @param funcName function name that the sending is serving for
     * @param request request message in binary format
     */
    private void sendToPeer(ZMQ.Socket peer, String peerId, String idChain,
                        String funcName, byte[] request) {
        peer.sendMore(peerId);
        peer.sendMore(NetUtils.DELIMITER);
        peer.sendMore(idChain);
        peer.sendMore(NetUtils.DELIMITER);
        peer.sendMore(funcName);
        peer.sendMore(NetUtils.DELIMITER);
        peer.send(request);
    }

    private void divideRequest(String sessionId, RequestMessage reqMsg, ZMQ.Socket peer,
                       String workerIdChain, String fwdWorkerId, String clientIdChain) {
        // we believe a function to split always have 2 parameters
        // the first is for data and the second is for data parser
        // byte[] packageData = (byte[]) reqMsg.inParams[0].values[0];
        byte[] packageData = getInParamByteValue(reqMsg.inParams[0]);

        // get the average worker value
        double avgWorkerValue = scheduler.getDistributionRate(workerIdChain);
        int jobActualNumber = (int) Math.floor(taskNumber * avgWorkerValue);
        actualTaskNumber += jobActualNumber;

        // send all the sub tasks to the worker
        for (int i = 0; i < jobActualNumber; i++) {
            // use data parser to divide the task
            byte[] dividedPkgData = dataParser.getPartFromObject(packageData, taskIndex, taskNumber);

            // create a sub task - called job
            RequestMessage jobReqMsg = reqMsg.clone();
            jobReqMsg.sessionId = sessionId;
            jobReqMsg.requestType = RequestMessage.RequestType.FORWARDING;
            jobReqMsg.inParams[0].values[0] = dividedPkgData;
            String[] ids = NetUtils.getLastClientId(workerIdChain);
            jobReqMsg.endWorkerId = ids[1];
            jobReqMsg.taskIndex = taskIndex;
            jobReqMsg.taskNumber = taskNumber;

            byte[] taskMsgBytes = NetUtils.serialize(jobReqMsg);

            // send to peer
            sendToPeer(peer, fwdWorkerId, clientIdChain, reqMsg.functionName, taskMsgBytes);
            
            NetUtils.printX("[Broker-" + brokerId + "] Forward Task #" + taskIndex + " To Worker [" + fwdWorkerId + "]",
                            NetUtils.TextColor.CYAN);
            taskIndex++;
        }
    }

    /**
     * Divides a request into parts.
     * We believe a function for this request should contain only 2 input
     * parameters, one for data and another is for data parser.
     * However, for the convenience, we create the data parser here at Broker
     *
     * @param sessionId
     * @param reqMsg
     * @param workerId
     *
     * @return
     */
    @SuppressWarnings("unused")
	private byte[] divideRequest(String sessionId, RequestMessage reqMsg, String workerId) {
    	// we believe a function to split always have 2 parameters
    	// the first is for data and the second is for data parser
    	// byte[] packageData = (byte[]) reqMsg.inParams[0].values[0];
    	byte[] packageData = getInParamByteValue(reqMsg.inParams[0]);

    	// get the average worker value
    	double avgWorkerValue = scheduler.getDistributionRate(workerId);

    	int firstOffset = 0, lastOffset = 0;

    	// use data parser to divide the task
    	byte[] dividedPkgData = dataParser.getPartFromObject(packageData, firstOffset, lastOffset);

    	// create a sub task - called job
    	RequestMessage jobReqMsg = reqMsg.clone();
    	jobReqMsg.inParams[0].values[0] = dividedPkgData;

    	return NetUtils.serialize(jobReqMsg);
    }

    /**
     * add a function list to the map, also add the new worker to the list
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

    		// update worker info list
    		for (WorkerInfo workerInfo : funcList[i].workerInfos) {
                scheduler.updateWorkerRecord(workerInfo.workerId, workerInfo);
            }
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

    private byte[] getInParamByteValue(InParam inParam) {
        switch (inParam.type) {
            case "java.lang.String": {
                return ((String) inParam.values[0]).getBytes();
            }
            case "byte[]": {
            	return (byte[]) inParam.values[0];
            }
        }
        return new byte[0];
    }
}
