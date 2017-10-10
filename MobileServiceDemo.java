package com.usu.tinyservice.classes;


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
	public int[] getFileList1(String path, int[] data, boolean fileOnly) {
		return new int[] { 1, 3, 5 };
	}
}
