package com.usu.tinyservice.rmi;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServiceBServer extends UnicastRemoteObject implements RmiServiceB {
	private static final long serialVersionUID = 1L;

	public RmiServiceBServer() throws RemoteException {
		super();
	}
	
	public static void main(String args[]) {
		try {
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.bind("129.123.7.172", new RmiServiceBServer());
			
			// Naming.rebind("//129.123.7.172/ServiceB", new RmiServiceBServer());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String[] sendData(String msg) throws RemoteException {
		return new String[] { "receive message: ", Integer.toString(msg.length()) };
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
