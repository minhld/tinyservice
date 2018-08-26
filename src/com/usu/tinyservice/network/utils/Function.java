package com.usu.tinyservice.network.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		Set<String> workerIds = new HashSet<>();
		for (int i = 0; i < workerInfos.size(); i++) {
			workerIds.add(workerInfos.get(i).workerId);
		}
		
		for (WorkerInfo wi : wis) {
			// get all sub IDs of a worker ID, for example, 
			// a worker ID: 1/2/3 will have sub IDs: 1, 1/2 and 1/2/3
			String[] subIds = getSubPaths(wi.workerId);
			
			// find if any of the sub IDs exist in the worker list, 
			// those IDs are the path that the REG message went through
			boolean subIdFound = false;
			for (String id : subIds) {
				if (workerIds.contains(id)) {
					subIdFound = true;
					break;
				}
			}
			
			if (!subIdFound) {
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
	
	public void setWorkerInfo(List<WorkerInfo> workerInfos) {
		this.workerInfos = workerInfos;
	}
	
	public WorkerInfo[] getWorkerInfos() {
		return this.workerInfos.toArray(new WorkerInfo[] {});
	}
	
	/**
	 * get all the sub paths of a full path
	 * 
	 * @param path
	 * @return
	 */
	private String[] getSubPaths(String path) {
		String[] parts = path.split("/");

		String[] subPaths = new String[parts.length];

		if (parts.length > 0) {		
			subPaths[0] = parts[0];
			
			for (int i = 1; i < parts.length; i++) {
				subPaths[i] = subPaths[i - 1] + "/" + parts[i];
			}
		}
		
		return subPaths;
	}
}
