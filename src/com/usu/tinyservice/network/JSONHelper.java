package com.usu.tinyservice.network;

import com.google.gson.Gson;
import com.usu.tinyservice.messages.JsonRequestMessage;
import com.usu.tinyservice.messages.JsonResponseMessage;

public class JSONHelper {
	
	public static String createRequest(JsonRequestMessage request) {
		Gson gson = new Gson();
		return gson.toJson(request);
	}
	
	public static JsonRequestMessage getRequest(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, JsonRequestMessage.class);
	}
	
	public static String createResponse(JsonResponseMessage response) {
		Gson gson = new Gson();
		return gson.toJson(response);
	}
	
	public static JsonResponseMessage getResponse(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, JsonResponseMessage.class);
	}
	
	
}
