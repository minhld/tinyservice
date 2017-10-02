package com.usu.tinyservice.classes;

import com.usu.tinyservice.annotations.MobileService;
import com.usu.tinyservice.annotations.ServiceMethod;

/**
 * this is pretty much a demo of how a Mobile Service looks like
 * 
 * @author minhld
 *
 */
@MobileService(version = "1.1")
public class MobileServiceDemo {
	@ServiceMethod
	public String getRoot() {
		return "D:\\";
	}
	
}
