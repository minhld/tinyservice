package com.usu.tinyservice.network.utils;

public class Function {
	public String functionName = "";
	public InputParam[] inParams = new InputParam[0];
	public OutputParam outParam = new OutputParam();
	public WorkerInfo[] workerInfos = new WorkerInfo[0];
	
	public Function(String functionName) {
		this.functionName = functionName;
	}
	
	public Function(String functionName, String[] inParamStrs, String outParamStr) {
		this.functionName = functionName;
		
		// add input parameters
		this.inParams = new InputParam[inParamStrs.length];
		for (int i = 0; i < inParamStrs.length; i++) {
			this.inParams[i] = new InputParam(inParamStrs[i]);
		}
		
		// add output parameters
		this.outParam = new OutputParam(outParamStr);
	}
}
