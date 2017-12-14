package com.usu.tinyservice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
/**
 * defines behaviors of a service method
 *  
 * @author minhld
 *
 */
public @interface ServiceMethod {
	public static final String DEFAULT_SUFFIX = "Worker"; 
	
	/*
	 * ------  synchronization mode ------
	 * defines whether the method is called in synchronous or 
	 * asynchronous mode. The default is asynchronous mode 
	 */
	SyncMode syncMode() default SyncMode.Async;
	
	/*
	 * ------ define suffix of the outcome service ------
	 * if developer specifies the suffix which is different
	 * from the default name, the new worker service will be
	 * created separately from the default one.
	 */
	String suffix() default DEFAULT_SUFFIX;
}
