package com.usu.tinyservice.network;

import org.zeromq.ZMQ;

/**
 * Publisher - push data to the brokers or subscribers
 * Supports two mode
 *
 * Created by minhld on 8/4/2016.
 */
public abstract class Publisher extends Thread {
    private ZMQ.Socket publisher;

    private int port = Constants.PUBLISH_PORT;
    private int sendInterval = Constants.PUBLISH_INTERVAL;
    private String groupIp = "*";
    private boolean neededBroker = false;

    public void setSendInterval(int interval) {
        this.sendInterval = interval;
    }

    public void setNeededBroker(boolean neededBroker) {
        this.neededBroker = neededBroker;
    }

    public Publisher() {
        this.start();
    }

    public Publisher(String groupIp) {
        this.groupIp = groupIp;
        this.start();
    }

    public Publisher(String groupIp, int port) {
        this.groupIp = groupIp;
        this.port = port;
        this.start();
    }

    public void run() {
        // prepare before running
        prepare();

        try {
            ZMQ.Context context = ZMQ.context(1);
            publisher = context.socket(ZMQ.PUB);
            String bindGroupStr = "tcp://" + this.groupIp + ":" + this.port;
            if (this.neededBroker) {
                // this will connect to a broker
                publisher.connect(bindGroupStr);
            } else {
                // this will set it as a self-control publisher
                publisher.bind(bindGroupStr);
            }

            // loop until the thread is disposed
            while (!Thread.currentThread().isInterrupted()) {
                send();

                // and sleep
                try {
                    Thread.sleep(this.sendInterval);
                } catch (Exception e) {
                }
            }

            publisher.close();
            context.term();
        } catch (Exception e) {
            // exception there - leave it for now
            e.printStackTrace();
        }
    }

    /**
     * this function should be implemented to provide preparation code
     * before the process is being run
     */
    protected abstract void prepare();

    /**
     * fill in the abstract send function to implement what the publisher
     * wants to send to the broker
     */
    protected abstract void send();

    protected void sendTopic(String topic) {
        publisher.sendMore(topic);
    }

    protected void sendMessage(byte[] msg) {
        publisher.send(msg);
    }

    protected void sendMessage(String msg) {
        publisher.send(msg);
    }

    protected void sendFrame(String topic, byte[] msg) {
        sendTopic(topic);
        sendMessage(msg);
    }

    protected void sendFrame(String topic, String msg) {
        sendTopic(topic);
        sendMessage(msg);
    }
}
