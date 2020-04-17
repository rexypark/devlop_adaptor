package com.indigo.esb.util;

import java.util.Map;

import org.apache.commons.collections.map.LRUMap;

public class OnlineMTMessageIDCache {
	
	Map msgCache;
	
	private static OnlineMTMessageIDCache cache = null;
	
	private OnlineMTMessageIDCache(int size){
		this.msgCache = new LRUMap(size, 0.5f);
	}
	private OnlineMTMessageIDCache(){
		this.msgCache = new LRUMap(2000, 0.5f);
	}
	public static synchronized OnlineMTMessageIDCache getInstance(int size){
		if(cache == null){
			cache = new OnlineMTMessageIDCache(size);
		}
		return cache;
	}
	public static synchronized OnlineMTMessageIDCache getInstance(){
		if(cache == null){
			cache = new OnlineMTMessageIDCache();
		}
		return cache;
	}
	
	public synchronized void put(String mtKey, Object info){
		this.msgCache.put(mtKey, info);
	}
	
	public synchronized Object get(String mtKey){
		return this.msgCache.get(mtKey);
	}
	
	public synchronized boolean isContain(String mtKey){
		return this.msgCache.containsKey(mtKey);
	}
	
	public synchronized void remove(String mtKey){
		this.msgCache.remove(mtKey);
	}
	
	
	
}
