package com.usu.tinyservice.utils;

public class Utils {
	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch(Exception e) { }
	}
}
