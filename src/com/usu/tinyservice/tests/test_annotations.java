package com.usu.tinyservice.tests;

import com.usu.tinyservice.annotations.MobileServiceProcessor;

public class test_annotations extends Thread {
	public void run() {
		MobileServiceProcessor processor = new MobileServiceProcessor();
		
	}
	
	public static void main(String args[]) {
		new test_annotations().start();
	}
}
