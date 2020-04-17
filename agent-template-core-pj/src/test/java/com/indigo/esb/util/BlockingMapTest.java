package com.indigo.esb.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class BlockingMapTest {
	
	private static final Logger log = Logger.getLogger(BlockingMapTest.class);

	public class CallableGetter implements Callable<Map<String, String>> {
		
		int timeout;

		public CallableGetter(int timeout) {
			this.timeout = timeout;
		}

		@Override
		public Map<String, String> call() throws Exception {
			Map<String, String> val = null;
			try {
				log.info("get started");
				int timeoutMilli = timeout * 1000;
				val = bm.get("1", timeoutMilli, TimeUnit.MILLISECONDS);
				log.info("get ended:" + val);
			} catch (InterruptedException e) {
				log.error(e);
			}
			return val;
		}
	}

	public class RunnablePutter implements Runnable {
		int delay;

		public RunnablePutter(int delay) {
			this.delay = delay;
		}

		@Override
		public void run() {
			Map<String, String> val = new HashMap<String, String>();
			val.put("putKey", "putVal");
			try {
				Thread.sleep(delay * 1000);
			} catch (InterruptedException e) {
				log.error("", e);
			}
			log.info("put started");
			bm.put("1", val);
			log.info("put ended:" + val);
		}
	}

	final BlockingMap<String, Map<String, String>> bm = new BlockingMap<String, Map<String, String>>();

	ExecutorService pool = Executors.newCachedThreadPool();

	@BeforeClass
	public static void setUp() throws Exception {
//		BasicConfigurator.configure();
	}
	

	@After
	public void tearDown() throws Exception {
		pool.shutdownNow();
	}

	@Test
	public void test() throws InterruptedException, ExecutionException, TimeoutException {
		int timeout = 10;
		Future<Map<String, String>> f = pool.submit(new CallableGetter(timeout));
		log.info("Getter submited");
//		Future<Map<String, String>> f = pool.invokeAll(new CallableGetter(timeout));

		int delay = 2;
		pool.submit(new RunnablePutter(delay));
		log.info("putter submited");

		Map<String, String> val = f.get(timeout, TimeUnit.SECONDS);
		log.info(val);
	}

}
