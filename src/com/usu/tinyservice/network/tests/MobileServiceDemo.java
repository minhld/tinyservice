package com.usu.tinyservice.network.tests;


import com.usu.tinyservice.annotations.MobileService;
import com.usu.tinyservice.annotations.TransmitType;
import com.usu.tinyservice.annotations.ServiceMethod;
import com.usu.tinyservice.annotations.SyncMode;

import java.io.File;
import java.util.Date;

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
		transmitType = TransmitType.Binary)
public class MobileServiceDemo {
	
	@ServiceMethod(
		syncMode = SyncMode.Async)
	public Data1[] getFileList1(String path, Data1[] data, boolean fileOnly) {
		Data1 data1 = new Data1();
		data1.data11 = new int[] { 1, 3, 5 };
		data1.data12 = new String[] { "hello", "there" }; 
		data1.data13 = ("[Time-" + new Date().toString() + "]").getBytes();
		return new Data1[] { data1 };
	}
	
	@ServiceMethod(
		syncMode = SyncMode.Async,
		suffix = "2")
	public String[] getFileList2(String path) {
		File folder = new File(path);
		File[] files = folder.listFiles();
		String[] res = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			res[i] = files[i].getAbsolutePath();
		}
		return res;
	}
}
