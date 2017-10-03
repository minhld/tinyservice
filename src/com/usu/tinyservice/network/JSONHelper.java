package com.usu.tinyservice.network;

import com.google.gson.Gson;
import com.usu.tinyservice.messages.RequestMessage;

public class JSONHelper {
	
	public static String createRequest(RequestMessage request) {
		Gson gson = new Gson();
		return gson.toJson(request);
	}
	
	public static RequestMessage getRequest(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, RequestMessage.class);
	}
	
	
}
