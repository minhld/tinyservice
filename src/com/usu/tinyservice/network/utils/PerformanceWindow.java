package com.usu.tinyservice.network.utils;

import java.util.HashMap;
import java.util.UUID;

/**
 * 
 * @author Minh Le
 *
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class PerformanceWindow {
	
	RingBuffer<String> windowBuffer;
	
	/**
	 * each item holds a list of (workerId - string, performance value - float)
	 */
	HashMap<String, HashMap<String, Performance>> window;
	
	public PerformanceWindow(int capacity) {
		windowBuffer = new RingBuffer<>(capacity);
		window = new HashMap<>();
	}
	
	public void update(String sId, String wId, float v) {
		HashMap<String, Performance> slice = window.get(sId);
		if (slice == null) {
			slice = new HashMap<>();
			
			// also remove the oldest session ID if the number of 
			// sessions exceeds the capacity of buffer
			String removedSessId = windowBuffer.add(sId);
			if (removedSessId != null) {
				window.remove(removedSessId);
			}
		}
		slice.put(wId, new Performance(sId, wId, v));
		window.put(sId, slice);
	}
	
	/**
	 * get the average value of each worker specified by ID
	 * 
	 * @param workerId
	 * @return
	 */
	public float getPerformance(String workerId) {
		HashMap[] perfList = window.values().toArray(new HashMap[] {});
		float avgValue = 0;
		int num = 0;
		Performance perf = null;
		for (HashMap map : perfList) {
			perf = (Performance) map.get(workerId);
			if (perf != null) {
				avgValue += perf.value;
				num++;
			}
		}
		avgValue = avgValue / num;
		return avgValue;
	}
	
	/**
	 * generates a 32 digit ID string
	 * @return
	 */
	public static String createSessionId() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 * an instance of Performance
	 * 
	 * @author Minh Le
	 *
	 */
	public class Performance {
		public String sessionId;
		public String workerId;
		public float value;
		
		public Performance(String _sId, String _wId, float _v) {
			this.sessionId = _sId;
			this.workerId = _wId;
			this.value = _v;
		}
	}
}
