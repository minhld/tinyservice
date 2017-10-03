package com.usu.tinyservice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface MobileService {
	// service version
	String version() default "1.0";
	
	// network model: could be either Pair-Pair, Client-Server, 
	// Publish-Subscribe or Push-Pull, default value is Client-Server
	NetworkModel networkModel() default NetworkModel.ClientServer;
	
	// data transmission type: could be either JSON or binary array
	// default value is JSON
	TransmitType transmitType() default TransmitType.JSON;
	
	// synchronization mode
	// default is Asynchronous
	SyncMode syncMode() default SyncMode.Async;
}
