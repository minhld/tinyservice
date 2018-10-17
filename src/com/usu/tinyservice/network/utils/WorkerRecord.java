package com.usu.tinyservice.network.utils;

/**
 * 
 * @author minhld
 *
 */
public class WorkerRecord {
	public static final int BUFFER_SIZE = 10;
	
	public String workerId;
	
	/**
	 * strength of the device Worker is running on. This is fixed
	 */
	public double strength;
	
	/**
	 * number of hops from the Worker to Broker. This is fixed
	 */
	public int hops;
	
	/**
	 * capacity of Worker, estimate cap = strength / hops;
	 */
	public double cap;
	
	/**
	 * number of sessions (tasks, may include multiple jobs) Worker receives
	 */
	public int steps = 0;
	
	/**
	 * recent average running time
	 */
	public double avg = 0;
	
	/**
	 * accumulate average running times over time
	 */
	public RingBuffer<Double> avgHist = new RingBuffer<>(BUFFER_SIZE);
	
	/**
	 * recent average running time of each job
	 * jobAvg = avg / jobNum
	 */
	public double jobAvg = 0;
	
	/**
	 * recent distribution percentage (value from 0 -> 1)
	 */
	public double perc = 0;
	
	/**
	 * accumulate history of distribution percentages over time
	 */
	public RingBuffer<Double> percHist = new RingBuffer<>(BUFFER_SIZE);
	
	/**
	 * average number of jobs that Worker recently received
	 */
	public double jobNum = 0;
	
	/**
	 * accumulate history of job numbers
	 */
	public RingBuffer<Double> jobNumHist = new RingBuffer<>(BUFFER_SIZE);
}
