package com.indigo.esb.util;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BlockingMap<K, V> {
	private Map<K, ArrayBlockingQueue<V>> map = new ConcurrentHashMap<K, ArrayBlockingQueue<V>>();

//	private BlockingQueue<V> getQueue(K key, boolean replace) {
//        return map.compute(key, (k, v) -> replace || v == null ? new ArrayBlockingQueue<>(1) : v);
//    }
	
	private BlockingQueue<V> getQueue(K key, boolean replace) {
		ArrayBlockingQueue<V> bq = null;
		
		//put
		if (replace) {
			bq = map.get(key);
			if(bq == null) {
				bq = new ArrayBlockingQueue<V>(1);
			}
			map.put(key, bq);
			return bq;
		}else { //get
			bq = map.get(key);
			if (bq == null) {
				bq = new ArrayBlockingQueue<V>(1);
				map.put(key, bq);
				return bq;
			} else {
				return bq;
			}
		}
	}

	public void put(K key, V value) {
		getQueue(key, true).add(value);
	}

	public V get(K key) throws InterruptedException {
		return getQueue(key, false).take();
	}

	public V get(K key, long timeout, TimeUnit unit) throws InterruptedException {
		return getQueue(key, false).poll(timeout, unit);
	}
}