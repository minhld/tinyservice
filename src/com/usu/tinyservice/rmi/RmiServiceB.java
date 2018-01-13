package com.usu.tinyservice.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiServiceB extends Remote {
    /**
     * 
     * @param msg
     * @return
     */
	public String[] sendData(String msg) throws RemoteException;

	/**
     * 
     * @param msg
     * @return
     */
	public String[] sendData2(String msg) throws RemoteException;

	/**
	 * 
	 * @param path
	 * @return
	 */
    public String[] getFolderList(String path) throws RemoteException;
}
