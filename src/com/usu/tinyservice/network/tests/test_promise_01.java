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
		 
		promise.done(result -> {
			System.out.println("Job done1: " + result);
			System.out.println("Job done2: " + result);
		})
		  .fail(rejection -> System.out.println("Job fail: " + rejection))
		  .progress(progress -> System.out.println("Job is in progress: " + progress))
		  .always((state, result, rejection) -> 
		    System.out.println("Job execution started: " + state + ", " + result + ", " + rejection));
		
		deferred.notify("notice");
		deferred.resolve("msg");
		// deferred.reject("oops");
	}
	
	
	
	public static void main(String args[]) {
		new test_promise_01().start();
	}
}
