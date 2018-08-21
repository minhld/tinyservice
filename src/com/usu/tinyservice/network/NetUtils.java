package com.usu.tinyservice.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.usu.tinyservice.messages.binary.OutParam;
import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.messages.json.JsonRequestMessage;
import com.usu.tinyservice.messages.json.JsonResponseMessage;
import com.usu.tinyservice.network.utils.Function;
import com.usu.tinyservice.network.utils.WorkerInfo;

public class NetUtils {
	public static enum WorkMode {
		NORMAL,
		FORWARD
	}
	
    public static final int WORKER_PORT = 6666;
    public static final int CLIENT_PORT = 6668;
    
    public static final String DEFAULT_IP = "*";
    public static final String SERVICE_1_0 = "1.0";
    
    public static final String EMPTY = "";
	public static final String DELIMITER = "";
    public static final String BROKER_INFO = "INFO";
    public static final String WORKER_REGISTER = "REGISTER";

    public static final String INFO_WORKER_NOT_READY = "WORKER_NOT_READY";
    public static final String INFO_REQUEST_SERVICES = "REQUEST_SERVICES";
    public static final String INFO_WORKER_FAILED = "WORKER_FAILED";
    
    private static Gson gson = new Gson();
	
    private static Random rand = new Random(System.currentTimeMillis());

        
    public static String getFunctions(List<String> functionList) {
    	String funcList = "";
    	for (String func : functionList) {
    		funcList += "," + func;
    	}
    	
    	// remove 
    	if (funcList.length() > 0) {
    		funcList = funcList.substring(1);
    	}
    	return funcList;
    }
    
//    public static Function[] getFunctionsFromJson(String functionsJson) {
//    	JsonArray jArray = jObject.getAsJsonArray("functions");
//        
//        List<Function> funcList = new ArrayList<>();
//        for (int i = 0; i < jArray.size(); i++) {
//        	Function f = NetUtils.gson.fromJson(jArray.get(i).toString(), Function.class);
//        	funcList.add(f);
//        }
//        return funcList.toArray(new Function[] {});
//    }
    
    public static String getFunctionJson(HashMap<String, HashMap<String, WorkerInfo>> functionMap) {
    	return gson.toJson(functionMap);
    }
    
    /**
     * get the list of <b>Functions</b> provided by a worker. 
     * 
     * @param workerInfoJson
     * @return
     */
    public static Function[] getFunctionsFromJson(String workerInfoJson) {
    	JsonElement jElement = new JsonParser().parse(workerInfoJson);
        JsonObject jObject = jElement.getAsJsonObject();
        JsonArray jArray = jObject.getAsJsonArray("functions");
        
        List<Function> funcList = new ArrayList<>();
        for (int i = 0; i < jArray.size(); i++) {
        	Function f = NetUtils.gson.fromJson(jArray.get(i).toString(), Function.class);
        	funcList.add(f);
        }
        return funcList.toArray(new Function[] {});
    }
    
    /**
     * get all functions provided by a worker
     * 
     * @param workerInfoJson worker info in JSON format
     * @return list of functions 
     * 
     */
    public static String[] getFunctions(String workerInfoJson) {
    	List<String> funcList = new ArrayList<>();
    	JsonElement jElement = new JsonParser().parse(workerInfoJson);
        JsonObject jObject = jElement.getAsJsonObject();
        JsonArray jArray = jObject.getAsJsonArray("functions");
        
        String funcName;
        for (int i = 0; i < jArray.size(); i++) {
        	funcName = jArray.get(i).getAsJsonObject().get("functionName").getAsString();
        	funcList.add(funcName);
        }
        
        return funcList.toArray(new String[] {});
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
    
}
