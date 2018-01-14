package com.usu.tinyservice.rmi;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.usu.tinyservice.network.NetUtils;

public class RmiServiceBServer extends UnicastRemoteObject implements RmiServiceB {
	private static final long serialVersionUID = 1L;

	public RmiServiceBServer() throws RemoteException {
		super();
	}
	
	public static void main(String args[]) {
		try {
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.bind("ServiceB", new RmiServiceBServer());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String[] sendData(String msg) throws RemoteException {
		// image detection mimic
    	int sleepTime = (int) (Math.random() * 200) + 100;
    	NetUtils.sleep(sleepTime);
		return new String[] { "receive message: ", Integer.toString(msg.length()) };
	}
	
	public String[] sendData2(String msg) throws RemoteException {
    	// image solving 
    	int sleepTime = (int) (Math.random() * 100) + 100;
    	NetUtils.sleep(sleepTime);
        return new String[] { msg, Integer.toString(msg.length()) };
    }

	public String[] getFolderList(String path) throws RemoteException {
        File folder = new File(path);
        File[] files = folder.listFiles();
        String[] res = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            res[i] = files[i].getAbsolutePath();
        }
        return res;
	}

}
