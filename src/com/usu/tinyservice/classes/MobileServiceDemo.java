package com.usu.tinyservice.classes;

import com.usu.tinyservice.annotations.MobileService;
import com.usu.tinyservice.annotations.ServiceDataType;
import com.usu.tinyservice.annotations.ServiceMethod;
import com.usu.tinyservice.annotations.ServiceNetworkType;

/**
 * this is pretty much a demo of how a Mobile Service looks like
 * 
 * @author minhld
 *
 */
@MobileService(
		version = "1.1", 
		networkType = ServiceNetworkType.ClientServer,
		dataType = ServiceDataType.JSON)
public class MobileServiceDemo {
	@ServiceMethod
	public String getRoot() {
		return "D:\\";
	}
	
}
