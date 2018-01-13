package com.usu.tinyservice.network.tests;

import java.io.File;

import com.usu.tinyservice.annotations.CommModel;
import com.usu.tinyservice.annotations.MobileService;
import com.usu.tinyservice.annotations.ServiceMethod;
import com.usu.tinyservice.annotations.SyncMode;
import com.usu.tinyservice.annotations.TransmitType;
import com.usu.tinyservice.network.NetUtils;

/**
 * Created by lee on 9/23/17.
 */
@MobileService(
        version = "1.2",
        commModel = CommModel.ClientServer,
        transmitType = TransmitType.Binary)
public class ServiceB {

    @ServiceMethod(syncMode = SyncMode.Async)
    public String[] sendData(String msg) {
        return new String[] { "receive message: ", Integer.toString(msg.length()) };
    }

    @ServiceMethod(syncMode = SyncMode.Async)
    public String[] sendData2(String msg) {
    	// image solving 
    	int sleepTime = (int) (Math.random() * 100) + 100;
    	NetUtils.sleep(sleepTime);
        return new String[] { msg, Integer.toString(msg.length()) };
    }
    
    @ServiceMethod(
            syncMode = SyncMode.Async,
            suffix = "2")
    public String[] getFolderList(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles();
        String[] res = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            res[i] = files[i].getAbsolutePath();
        }
        return res;
    }
}