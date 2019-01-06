package com.usu.tinyservice.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.google.gson.Gson;
import com.usu.tinyservice.messages.binary.OutParam;
import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.messages.json.JsonRequestMessage;
import com.usu.tinyservice.network.utils.Function;
import com.usu.tinyservice.network.utils.RegInfo;

public class NetUtils {
	public enum WorkMode {
		NORMAL,
		FORWARD
	}

	public enum TextColor {
		RED,
		GREEN,
		YELLOW,
		BLUE,
		PURPLE,
		CYAN,
		WHITE
	}
	
    public static final int WORKER_PORT = 6666;
    public static final int CLIENT_PORT = 6668;
    
    public static final String DEFAULT_IP = "*";
    public static final String SERVICE_1_0 = "1.0";
    
    public static final String EMPTY = "";
	public static final String DELIMITER = "";
    public static final String BROKER_INFO = "INFO";
    public static final String WORKER_REGISTER = "REGISTER";
    public static final String WORKER_FORWARD = "FORWARD";

    public static final String INFO_WORKER_NOT_READY = "WORKER_NOT_READY";
    public static final String INFO_REQUEST_SERVICES = "REQUEST_SERVICES";
    public static final String INFO_WORKER_FAILED = "WORKER_FAILED";
    public static final String INFO_WORKERS_READY = "WORKERS_READY";
    
    private static Gson gson = new Gson();
	
    private static Random rand = new Random(System.currentTimeMillis());
    
    /**
     * convert function list to JSON string
     * 
     * @param id
	 * @param functions
	 *
     * @return
     */
    public static String createForwardMessage(String id, Function[] functions) {
    	RegInfo regInfo = new RegInfo();
    	regInfo.code = "FORWARD";
    	regInfo.id = id;
    	regInfo.functions = functions;
    	return gson.toJson(regInfo);
    }
    
    /**
     * convert a register info to JSON
     * 
     * @param regInfo
     * @return
     */
    public static String createForwardMessage(RegInfo regInfo) {
    	return gson.toJson(regInfo);
    }
    
    /**
     * get the list of <b>Functions</b> provided by a worker. 
     * 
     * @param regInfoJson
     * @return
     */
    public static RegInfo getRegInfo(String regInfoJson) {
    	return gson.fromJson(regInfoJson, RegInfo.class);
    }
    
    /**
     * create a simple message for requesting info from broker or worker 
     * 
     * @param info
     * @return
     */
    public static byte[] createMessage(String info) {
    	ResponseMessage respMsg = new ResponseMessage(NetUtils.BROKER_INFO);
    	respMsg.outParam = new OutParam("java.lang.String");
    	respMsg.outParam.values = new Object[1];
    	respMsg.outParam.values[0] = info;
    	return NetUtils.serialize(respMsg);
    }
    
    /**
     * concatenates new client ID to the ID chain
     * 
     * @param chain
     * @param clientId
     * @return
     */
    public static String concatIds(String chain, String clientId) {
    	return chain.equals(NetUtils.EMPTY) ? clientId : chain + "/" + clientId;
    }
    
    /**
     * get the last client ID in the ID chain. Also returns the shorten 
     * ID chain which excluded the last ID
     * 
     * @param chain
     * @return a string array including 2 items.<br/>
     * 	- first item: last client ID<br/>
     * 	- second item: shorten ID chain<br/>
     * 
     */
    public static String[] getLastClientId(String chain) {
    	int lastIdx = chain.lastIndexOf("/");
    	if (chain == null || chain.isEmpty()) {
    		return new String[] { NetUtils.EMPTY, NetUtils.EMPTY }; 
    	} else if (!chain.isEmpty() && lastIdx < 0) {
    		return new String[] { chain, NetUtils.EMPTY }; 
    	} else {
	    	String newChain = chain.substring(0, lastIdx);
	    	String lastClientId = chain.substring(lastIdx + 1);
	    	return new String[] { lastClientId, newChain };
    	}
    }
    
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
		return gson.toJson(request);
	}
	
	public static JsonRequestMessage getRequest(String json) {
		return gson.fromJson(json, JsonRequestMessage.class);
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
    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream b = null;
        try {
        	b = new ByteArrayInputStream(bytes);
	        ObjectInputStream o = new ObjectInputStream(b);
	        return o.readObject();
        } catch (Exception e) {
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
     * set random ID for the specific socket
     * 
     * @param sock
     */
    public static void setId(Socket sock) {
        String identity = generateId();
        sock.setIdentity(identity.getBytes(ZMQ.CHARSET));
    }

    /**
     * generates a random string in the format XXXXX-XXXXX
     * so that it could be used as an ID of a component
     * 
     * @return
     */
    public static String generateId() {
    	return String.format("%04X-%04X", rand.nextInt(), rand.nextInt());
    }
    
    /**
     * sleep the current thread for a certain amount of time
     * 
     * @param time
     */
    public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch(Exception e) { }
	}
    
    public static void print(String msg) {
		System.out.println(msg);
    }
    
    public static void printX(String msg) {
    	System.err.println(msg);
    }

	public static void printX(String msg, TextColor color) {
    	String selectedColor = "";
    	String resetColor = "\u001B[0m";
    	switch (color) {
			case RED: {
				selectedColor = "\u001B[31m";
				break;
			}
			case GREEN: {
				selectedColor = "\u001B[32m";
				break;
			}
			case YELLOW: {
				selectedColor = "\u001B[33m";
				break;
			}
			case BLUE: {
				selectedColor = "\u001B[34m";
				break;
			}
			case PURPLE: {
				selectedColor = "\u001B[35m";
				break;
			}
			case CYAN: {
				selectedColor = "\u001B[36m";
				break;
			}
			case WHITE: {
				selectedColor = "\u001B[37m";
				break;
			}
		}
		System.err.println(selectedColor + msg + resetColor);
	}
}
