package com.usu.tinyservice.tests;

import com.usu.tinyservice.annotations.MobileService;

@MobileService(version="1.2")
public class User {
	// guide: http://www.baeldung.com/java-annotation-processing-builder 
	
	private String name;
	private String city;
}