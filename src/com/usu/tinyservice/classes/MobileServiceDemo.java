package com.usu.tinyservice.classes;

import java.io.File;

import com.usu.tinyservice.annotations.MobileService;
import com.usu.tinyservice.annotations.TransmitType;
import com.usu.tinyservice.annotations.ServiceMethod;
import com.usu.tinyservice.annotations.SyncMode;
import com.usu.tinyservice.annotations.CommModel;

/**
 * this is pretty much a demo of how a Mobile Service looks like
 * 
 * @author minhld
 *
 */
@MobileService(
		version = "1.1", 
		commModel = CommModel.ClientServer,
		transmitType = TransmitType.JSON)
public class MobileServiceDemo {
	@ServiceMethod(syncMode = SyncMode.Async)
	public String getRoot() {
		return "D:\\";
	}
	
	@ServiceMethod(syncMode = SyncMode.Async)
	public String[] getFileList(String path, boolean fileOnly) {
		File pathFile = new File(path);
		return pathFile.list();
	}
	
	@ServiceMethod(syncMode = SyncMode.Async)
	public int[] getFileList2(String path, int[] count, boolean fileOnly) {
		// File pathFile = new File(path);
		return new int[] { 1, 5, 3, 7, 9};
	}

}
