package com.usu.tinyservice.network.utils;

import java.util.ArrayList;
import java.util.List;

public class Function {
	public String functionName = "";
	public String[] inParams = new String[0];
	public String outParam = "";
	public List<WorkerInfo> workerInfos = new ArrayList<>();
	
	public Function(String functionName) {
		this.functionName = functionName;
	}
	
	public Function(String functionName, String[] inParamStrs, String outParamStr) {
		this.functionName = functionName;
		this.inParams = inParamStrs;
		this.outParam = outParamStr;
	}
	
	public void addWorkerInfos(List<WorkerInfo> wis) {
		List<String> workerIds = new ArrayList<>();
		for (int i = 0; i < workerInfos.size(); i++) {
			workerIds.add(workerInfos.get(i).workerId);
		}
		
		for (WorkerInfo wi : wis) {
			if (!workerIds.contains(wi.workerId)) {
				workerInfos.add(wi);
			}
		}
	}
	
	public void addWorkerInfos(WorkerInfo[] wis) {
		List<String> workerIds = new ArrayList<>();
		for (int i = 0; i < workerInfos.size(); i++) {
			workerIds.add(workerInfos.get(i).workerId);
		}
		
		for (int i = 0; i < wis.length; i++) {
			if (!workerIds.contains(wis[i].workerId)) {
				workerInfos.add(wis[i]);
			}
		}
	}
	
	public WorkerInfo[] getWorkerInfos() {
		return workerInfos.toArray(new WorkerInfo[] {});
	}
}
