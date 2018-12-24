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
	
	public static boolean isNewWorkerJoined = false;
	
	public WorkerScheduler() {
		workerRecords = new HashMap<>();
	}

	/**
	 * this is called when broker has finished one session
	 * all the flags will be reset
	 *
	 * @param sessionId
	 */
	public void finalizeSession(String sessionId) {
		// reset the flag
		WorkerScheduler.isNewWorkerJoined = false;
	}

	/**
	 * to update worker records, will add new record if it does not
	 * exist in the scheduler before
	 *
	 * @param workerId
	 * @param wInfo
	 */
	public void updateWorkerRecord(String workerId, WorkerInfo wInfo) {
		WorkerRecord wRec = mapWorkerInfoToRecord(wInfo);
		updateWorkerRecord(workerId, wRec);
	}

	/**
	 * to update worker records, will add new record if it does not
	 * exist in the scheduler before
	 *
	 * @param workerId
	 * @param wRec
	 */
	public void updateWorkerRecord(String workerId, WorkerRecord wRec) {
		// retrieve the existing worker record
		WorkerRecord existWorkerRec = workerRecords.get(workerId);

		if (existWorkerRec == null || existWorkerRec.steps == 0) {
			wRec.capacity = wRec.strength / wRec.hops;
			workerRecords.put(workerId, wRec);

			// setup flag
			isNewWorkerJoined = true;

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
			existWorkerRec.jobAvgTime = existWorkerRec.jobNum != 0 ?
						existWorkerRec.avgTime / existWorkerRec.jobNum : 0;
			existWorkerRec.steps++;
			
			// re-estimate the total job average time
			estimateWorkerPerformance(null);
			
		}
	}

	/**
	 * get distribution rate of each worker
	 * 
	 * @param workerId
	 *
	 * @return distribution rate of each worker
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

	WorkerRecord mapWorkerInfoToRecord(WorkerInfo wInfo) {
		WorkerRecord wRec = new WorkerRecord();
		wRec.workerId = wInfo.workerId;
		wRec.strength = wInfo.strength;
		wRec.hops = wInfo.hops;
		return wRec;
	}

	/**
	 * this function estimate worker function
	 * @param workerId
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
