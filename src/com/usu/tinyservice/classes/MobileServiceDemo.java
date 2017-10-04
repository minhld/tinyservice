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
	@ServiceMethod(syncMode = SyncMode.Sync)
	public String getRoot() {
		return "D:\\";
	}
	
	@ServiceMethod(syncMode = SyncMode.Async)
	public String[] getFileList(String path, boolean fileOnly) {
		File pathFile = new File(path);
		return pathFile.list();
	}
}
