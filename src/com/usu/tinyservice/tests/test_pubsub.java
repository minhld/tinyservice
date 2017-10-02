package com.usu.tinyservice.tests;

import java.util.Date;

import com.usu.tinyservice.network.Publisher;
import com.usu.tinyservice.network.Subscriber;
import com.usu.tinyservice.network.Utils;

public class test_pubsub extends Thread {
	public void run() {
		// create publisher
		new ExPublisher();
		
		// create subscriber
		Subscriber subscriber = new Subscriber("*", Utils.PUBLISH_PORT, new String[] { "video_frame" });
        subscriber.setMessageListener(new Subscriber.MessageListener() {
            @Override
            public void msgReceived(String topic, final byte[] msg) {
                System.out.println(new Date().toString() + ": " + new String(msg));
            }
        });
	}
	
	public class ExPublisher extends Publisher {
        
        public ExPublisher() {
            super("*");
            this.setSendInterval(10);
        }

        @Override
        protected void prepare() {
            
        }

        @Override
        public void send() {
        	String testStr = "test_" + Math.round(Math.random() * 10000);
            sendFrame("video_frame", testStr.getBytes());
        }
    }
	
	public static void main(String args[]) {
		new test_pubsub().start();
	}
}
