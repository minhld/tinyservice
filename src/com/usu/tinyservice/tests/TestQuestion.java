package com.usu.tinyservice.tests;

public class TestQuestion {
	static int x = 50;
	 
	public static void anotherMethod() {
		System.out.println(x);
		x = 10;
	}
		
	void TestMe() {
		x = 25;
		anotherMethod();
	}
		
	public static void main(String[] args) {
		TestQuestion obj = new TestQuestion();
		System.out.println(x);
		obj.TestMe();
		System.out.println(x);
	}
}
