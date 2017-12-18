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
    private String groupIp = "*";
    private int port = NetUtils.SERVER_PORT;

//    private ExAckClient ackClient;

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

    public void run() {
        initWithBroker();
    }

    /**
     * initiate worker with broker at the middle
     */
    private void initWithBroker() {
        try {
            ZMQ.Context context = ZMQ.context(1);

            //  Socket to talk to clients and set its Id
            ZMQ.Socket worker = context.socket(ZMQ.REQ);
            NetUtils.setId (worker);
            this.workerId = new String(worker.getIdentity());
            worker.connect("tcp://" + this.groupIp + ":" + this.port);

            // to report worker has finished the initialization
            // workerStarted(this.workerId);
            System.out.println("[Worker-" + workerId + "] Started.");

            // inform broker that i am ready
            // worker.send(NetUtils.WORKER_READY);
            String registerInfo = info();
            worker.send(registerInfo);
            
//            // initiate ACK client - to listen to DRL request from brokers
//            ackClient = new ExAckClient(context, this.groupIp, worker.getIdentity());

            // this part is to wait for broker to send job to execute
            String clientAddr;
            byte[] request, result, empty;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // set start job clock
                    long startTime = System.currentTimeMillis();

                    // get client address
                    clientAddr = worker.recvStr();

                    // delimiter
                    empty = worker.recv();
                    assert (empty.length == 0);

                    // get request, send reply
                    request = worker.recv();
                    result = resolveRequest(request);

                    // return result back to front-end
                    worker.sendMore(clientAddr);
                    worker.sendMore(NetUtils.DELIMITER);
                    worker.send(result);

                    // end the job execution clock
                    long durr = System.currentTimeMillis() - startTime;
                    TaskDone taskInfo = new TaskDone();
                    taskInfo.durration = durr;
                    
                    // when worker completes the task
                    // workerFinished(workerId, taskInfo);
                    System.out.println("[Worker-" + workerId + "] Completed In " + taskInfo.durration + "ms");

                } catch (Exception d) {
                    d.printStackTrace();
                    worker.sendMore(NetUtils.WORKER_FAILED);
                    worker.sendMore(NetUtils.DELIMITER);
                    worker.send(workerId);

                }
            }
            worker.close();
            context.term();
        } catch (Exception e) {
            // exception there - leave it for now
            e.printStackTrace();
        }
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

//    /**
//     * similar to the <i>clientStarted</i> event in client, this event will be happened
//     * when worker completes the initiation.
//     *
//     * @param workerId
//     */
//    public abstract void workerStarted(String workerId);

    
//    /**
//     * 
//     * @param workerId
//     */
//    public abstract void register(String workerId);
    
//    /**
//     * this event occurs when worker finishes the current work
//     * 
//     * @param workerId
//     * @param taskDone
//     */
//    public abstract void workerFinished(String workerId, TaskDone taskDone);

    /**
     * this abstract function needs to be filled. this is to
     * define how worker will complete the work
     *
     * @param packageBytes
     * @return
     */
    public abstract byte[] resolveRequest(byte[] packageBytes);

    /**
     * this event occurs when worker has completed receiving a task from client
     *
     * @param clientId
     * @param dataSize
     */
    public abstract void receivedTask(String clientId, int dataSize);

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
