package com.usu.tinyservice.network.utils;

public class WorkerInfo {
	public String workerId = "";
	public float strength = 1f;
	public int hops = 1;
	
	public WorkerInfo(String workerId) {
		this.workerId = workerId;
	}
	
	public WorkerInfo(String workerId, float strength) {
		this.workerId = workerId;
		this.strength = strength;
	}
	
	public WorkerInfo(String workerId, float strength, int hops) {
		this.workerId = workerId;
		this.strength = strength;
		this.hops = hops;
	}
}
