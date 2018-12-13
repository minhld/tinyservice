package com.usu.tinyservice.network.tests;

import org.jdeferred2.Deferred;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.impl.DeferredObject;

public class test_promise_01 extends Thread {
	
	public void run() {
		
		Deferred<String, String, String> deferred
		  = new DeferredObject<>();
		Promise<String, String, String> promise = deferred.promise();
		 
		promise.then(msg -> {
			System.out.println("Job started: " + msg + ", " + getName());
			  try {
				  Thread.sleep(3000);
			  } catch(Exception e) { }
		})
		 .done(msg -> {
			System.out.println("Job done: " + msg + ", " + getName());
		}).fail(rejection -> System.out.println("Job fail: " + rejection))
		  .progress(progress -> {
			  System.out.println("Job is in progress: " + progress);			  
		  })
		  .always((state, result, rejection) -> 
		    System.out.println("Job execution started: " + state + 
		    		", " + result + ", " + rejection + "," + getName()));
		
		// deferred.notify("notice");
		deferred.resolve("msg");
		// deferred.reject("oops");
		
		System.out.println("Current thread: " + this.getName());
	}
	
	
	
	public static void main(String args[]) {
		new test_promise_01().start();
	}
}
