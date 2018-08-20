package com.usu.tinyservice.network.utils;

public class Function {
	public String functionName = "";
	public String[] inParams = new String[0];
	public String outParam = "";
	public WorkerInfo[] workerInfos = new WorkerInfo[0];
	
	public Function(String functionName) {
		this.functionName = functionName;
	}
	
	public Function(String functionName, String[] inParamStrs, String outParamStr) {
		this.functionName = functionName;
		this.inParams = inParamStrs;
		this.outParam = outParamStr;
	}
}
