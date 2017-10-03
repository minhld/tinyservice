package com.usu.tinyservice.classes;

import java.io.File;

import com.usu.tinyservice.annotations.MobileService;
import com.usu.tinyservice.annotations.ServiceTransmitType;
import com.usu.tinyservice.annotations.ServiceMethod;
import com.usu.tinyservice.annotations.ServiceNetworkModel;

/**
 * this is pretty much a demo of how a Mobile Service looks like
 * 
 * @author minhld
 *
 */
@MobileService(
		version = "1.1", 
		networkModel = ServiceNetworkModel.ClientServer,
		transmitType = ServiceTransmitType.JSON)
public class MobileServiceDemo {
	@ServiceMethod
	public String getRoot() {
		return "D:\\";
	}
	
	@ServiceMethod
	public String[] getFileList(String path, boolean fileOnly) {
		File pathFile = new File(path);
		return pathFile.list();
	}
}
