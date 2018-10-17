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
		
		if (existWorkerRec == null) {
			workerRecords.put(workerId, wRec);
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
			existWorkerRec.cap = existWorkerRec.strength / existWorkerRec.hops;
			existWorkerRec.avg = ((existWorkerRec.avg * existWorkerRec.steps) + wRec.avg) / (existWorkerRec.steps + 1);
			existWorkerRec.avgHist.add(wRec.avg);
			existWorkerRec.percHist.add(wRec.perc);
			existWorkerRec.jobNum = ((existWorkerRec.jobNum * existWorkerRec.steps) + wRec.jobNum) / (existWorkerRec.steps + 1);
			existWorkerRec.jobNumHist.add(wRec.jobNum);
			existWorkerRec.jobAvg = existWorkerRec.avg / existWorkerRec.jobNum;
			// existWorkerRec.perc = 0;
			existWorkerRec.steps++;
			
			// re-estimate performance of all worker and give out the appropriate performance
			estimateWorkerPerformance(null);
			
		}
	}
	
	/**
	 * this function estimate worker function
	 * @param new worker's ID
	 */
	public void estimateWorkerPerformance(String workerId) {
		if (workerId != null) {
			// new worker is added, will estimate by its capacity
		} else {
			// no new worker is added, will use their job average time to estimate
		}
	}
}
