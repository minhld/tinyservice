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
	
	public void add(T item) {
		if (queue.size() == this.capacity) {
			queue.removeFirst();
		}
		queue.addLast(item);
	}
	
	public T get() {
		return queue.getFirst();
	}
	
	public int size() {
		return queue.size();
	}
}
