package com.usu.tinyservice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface MobileService {
	String version() default "1.0";
	ServiceNetworkType networkType() default ServiceNetworkType.ClientServer;
	ServiceDataType dataType() default ServiceDataType.JSON;
}
