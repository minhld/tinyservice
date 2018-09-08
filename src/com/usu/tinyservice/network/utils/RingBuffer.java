package com.usu.tinyservice.network.utils;

import java.util.LinkedList;

/**
 * 
 * 
 * @author Minh Le
 *
 * @param <T>
 */
public class RingBuffer <T>{
	private LinkedList<T> queue;
	private int capacity;
	
	public RingBuffer(int capacity) {
		this.capacity = capacity;
		queue = new LinkedList<>();
	}
	
	/**
	 * add one item to the last. this function will also remove 
	 * the first item if the item number exceeds the capacity and
	 * return the first item as output
	 * 
	 * @param item
	 * @return
	 */
	public T add(T item) {
		T ret = null;
		if (queue.size() == this.capacity) {
			ret = queue.removeFirst();
		}
		queue.addLast(item);
		
		return ret;
	}
	
	public T get() {
		return queue.getFirst();
	}
	
	public int size() {
		return queue.size();
	}
}
