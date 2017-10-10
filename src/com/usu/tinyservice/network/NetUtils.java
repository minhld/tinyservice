package com.usu.tinyservice.network;

import java.lang.reflect.Array;
import java.net.Proxy.Type;
import java.util.Arrays;
import java.util.List;

public class NetUtils {
	
	public static <T> String[] getString(T inputArray) {
    	String[] rets = new String[1];
    	rets[0] = inputArray.toString();
    	return rets;
    }
	
	public static <T> String[] getStringArray(T inputParam) {
		inputParam.getClass().isArray();
		String[] rets = new String[1];
    	rets[0] = inputParam.toString();
    	return rets;
	}
	
    public static <T> String[] getStringArrays(T[] inputArray) {
    	String[] rets = new String[inputArray.length];
    	for (int i = 0; i < rets.length; i++) {
    		rets[i] = inputArray[i].toString();
    	}
    	return rets;
    }

}
