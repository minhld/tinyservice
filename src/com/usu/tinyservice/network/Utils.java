package com.usu.tinyservice.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Created by minhld on 9/29/2017.
 */
public class Utils {
	public static final String SERVER_GENERAL_IP = "*";
    public static final int PUBLISH_PORT = 8001;
    public static final int SUBSCRIBE_PORT = 8002;
    public static final int REQUEST_PORT = 8003;
    public static final int RESPONSE_PORT = 8006;
    public static final int PUBLISH_INTERVAL = 10;
    
    public static byte[] object2ByteArray(Object obj) {
    	ByteArrayOutputStream bos = null;
    	try {
    		bos = new ByteArrayOutputStream();
    		ObjectOutput out = new ObjectOutputStream(bos);   
    		out.writeObject(obj);
    		out.flush();
    		return bos.toByteArray();
    	} catch (IOException e) {
    		e.printStackTrace();
    		// return an empty array
    		return new byte[0];
    	} finally {
    		try {
    			if (bos != null) {
    				bos.close();
    			}
    		} catch (IOException ex) { }
    	}
    }
}
