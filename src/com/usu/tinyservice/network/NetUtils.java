package com.usu.tinyservice.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.google.gson.Gson;
import com.usu.tinyservice.messages.json.JsonRequestMessage;
import com.usu.tinyservice.messages.json.JsonResponseMessage;

public class NetUtils {
	
	public static <T> String[] getString(T inputArray) {
    	String[] rets = new String[1];
    	rets[0] = inputArray.toString();
    	return rets;
    }
	
	public static <T> String[] getStringArray(T inputParam) {
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

    public static String[] getStringArray(byte[] inParams) {
    	String[] rets = new String[inParams.length];
    	for (int i = 0; i < inParams.length; i++) {
    		rets[i] = Byte.toString(inParams[i]);
    	}
    	return rets;
    }
    
    public static String[] getStringArray(char[] inParams) {
    	String[] rets = new String[inParams.length];
    	for (int i = 0; i < inParams.length; i++) {
    		rets[i] = Character.toString(inParams[i]);
    	}
    	return rets;
    }
    
    public static String[] getStringArray(short[] inParams) {
    	String[] rets = new String[inParams.length];
    	for (int i = 0; i < inParams.length; i++) {
    		rets[i] = Short.toString(inParams[i]);
    	}
    	return rets;
    }
    
    public static String[] getStringArray(int[] inParams) {
    	String[] rets = new String[inParams.length];
    	for (int i = 0; i < inParams.length; i++) {
    		rets[i] = Integer.toString(inParams[i]);
    	}
    	return rets;
    }
    
    public static String[] getStringArray(long[] inParams) {
    	String[] rets = new String[inParams.length];
    	for (int i = 0; i < inParams.length; i++) {
    		rets[i] = Long.toString(inParams[i]);
    	}
    	return rets;
    }
    
    public static String[] getStringArray(float[] inParams) {
    	String[] rets = new String[inParams.length];
    	for (int i = 0; i < inParams.length; i++) {
    		rets[i] = Float.toString(inParams[i]);
    	}
    	return rets;
    }
    
    public static String[] getStringArray(double[] inParams) {
    	String[] rets = new String[inParams.length];
    	for (int i = 0; i < inParams.length; i++) {
    		rets[i] = Double.toString(inParams[i]);
    	}
    	return rets;
    }
    
    public static String[] getStringArray(boolean[] inParams) {
    	String[] rets = new String[inParams.length];
    	for (int i = 0; i < inParams.length; i++) {
    		rets[i] = Boolean.toString(inParams[i]);
    	}
    	return rets;
    }
    
    /**
     * convert from a primitive type to Object type and return
     * result in the string format 
     * 
     * @param type
     * @return
     */
    public static String convertType(String type) {
		switch (type) {
			case "byte": {
				return "Byte";
			}
			case "char": {
				return "Character";
			}
			case "short": {
				return "Short";
			}
			case "int": {
				return "Integer";
			}
			case "long": {
				return "Long";
			}
			case "float": {
				return "Float";
			}
			case "double": {
				return "Double";
			}
			case "boolean": {
				return "Boolean";
			}
			default: {
				return "String";
			}
			
		}
	}
    
    public static String createRequest(JsonRequestMessage request) {
		Gson gson = new Gson();
		return gson.toJson(request);
	}
	
	public static JsonRequestMessage getRequest(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, JsonRequestMessage.class);
	}
	
	public static String createResponse(JsonResponseMessage response) {
		Gson gson = new Gson();
		return gson.toJson(response);
	}
	
	public static JsonResponseMessage getResponse(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, JsonResponseMessage.class);
	}
	
	/**
     * serialize an object to binary array
     *
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] serialize(Object obj) {
    	ByteArrayOutputStream b = null;
    	try {
	        b = new ByteArrayOutputStream();
	        ObjectOutputStream o = new ObjectOutputStream(b);
	        o.writeObject(obj);
	        o.flush();
	        return b.toByteArray();
    	} catch (IOException e) {
    		e.printStackTrace();
    		return new byte[0];
    	} finally {
    		try {
    			if (b!= null) {
    				b.close();
    			}
    		} catch (IOException ex) { }
    	}
    }

    /**
     * deserialize an object from a binary array
     *
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(byte[] bytes) throws Exception {
        ByteArrayInputStream b = null;
        try {
        	b = new ByteArrayInputStream(bytes);
	        ObjectInputStream o = new ObjectInputStream(b);
	        return o.readObject();
        } catch (IOException e) {
    		e.printStackTrace();
    		return new byte[0];
    	} finally {
    		try {
    			if (b!= null) {
    				b.close();
    			}
    		} catch (IOException ex) { }
    	}
    }

}
