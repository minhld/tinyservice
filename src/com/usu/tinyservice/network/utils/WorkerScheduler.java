package com.usu.tinyservice.network.utils;

import java.util.HashMap;

/**
 * this class keeps track of worker performance and 
 * schedule for history and predict the configuration 
 * for the next run
 * 
 * @author minhld
 * @since oct 16, 2018
 *
 */
public class WorkerScheduler {
	/**
	 * holds a list of worker records, search each record
	 * by worker ID
	 */
	HashMap<String, WorkerRecord> workerRecords;
	
	double totalCapacity = 0d;
	double totalJobAvgTime = 0d;
	
	boolean isNewWorkerJoined = false;
	
	public WorkerScheduler() {
		workerRecords = new HashMap<>();
	}
	
	/**
	 * to update worker records, will add new record if it does not 
	 * exist in the scheduler before
	 * 
	 * @param workerId
	 */
	public void updateWorkerRecord(String workerId, WorkerRecord wRec) {
		// retrieve the existing worker record
		WorkerRecord existWorkerRec = workerRecords.get(workerId);
		
		isNewWorkerJoined = (existWorkerRec == null);
		
		if (existWorkerRec == null) {
			workerRecords.put(workerId, wRec);
			
			// re-estimate the total capacity
			estimateWorkerPerformance(workerId);
			
		} else {
			// update strength if its value changed
			if (wRec.strength > 0) {
				existWorkerRec.strength = wRec.strength;
			}

			// update hops
			if (wRec.hops > 0) {
				existWorkerRec.hops = wRec.hops;
			}
			
			// update cap if either of above values changed
			existWorkerRec.capacity = existWorkerRec.strength / existWorkerRec.hops;
			existWorkerRec.avgTime = ((existWorkerRec.avgTime * existWorkerRec.steps) + wRec.avgTime) / (existWorkerRec.steps + 1);
			existWorkerRec.avgTimeHist.add(wRec.avgTime);
			existWorkerRec.distRateHist.add(wRec.distRate);
			existWorkerRec.jobNum = ((existWorkerRec.jobNum * existWorkerRec.steps) + wRec.jobNum) / (existWorkerRec.steps + 1);
			existWorkerRec.jobNumHist.add(wRec.jobNum);
			existWorkerRec.jobAvgTime = existWorkerRec.avgTime / existWorkerRec.jobNum;
			existWorkerRec.steps++;
			
			// re-estimate the total job average time
			estimateWorkerPerformance(null);
			
		}
	}

	/**
	 * get distribution rate of each worker
	 * 
	 * @param workerId
	 * @param isNewWorkerJoined
	 * @return
	 */
	public double getDistributionRate(String workerId) {
		WorkerRecord workerRecord = workerRecords.get(workerId);
		
		if (this.isNewWorkerJoined) {
			// if the new worker joins, rate will be based on capacity
			return workerRecord.capacity / totalCapacity;
		} else {
			// if no worker joins, rate will base on average times
			return (1 / workerRecord.jobAvgTime) / totalJobAvgTime; 
		}
	}
	
	/**
	 * this function estimate worker function
	 * @param new worker's ID
	 */
	void estimateWorkerPerformance(String workerId) {
		if (workerId != null) {
			// when there is a new worker joining 
			for (WorkerRecord rec : workerRecords.values()) {
				totalCapacity += rec.capacity;
			}
		} else {
			// when there is no new worker joining
			for (WorkerRecord rec : workerRecords.values()) {
				totalJobAvgTime += 1 / rec.jobAvgTime;
			}
		}
	}
}
