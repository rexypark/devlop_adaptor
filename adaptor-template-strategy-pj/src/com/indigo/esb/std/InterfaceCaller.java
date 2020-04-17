package com.indigo.esb.std;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

public class InterfaceCaller {

	private static Logger logger = LoggerFactory.getLogger(InterfaceCaller.class);

	protected ExecutorService executorService;
	protected int serviceCount = 2;

	@Resource
	BlockingQueue<Object> inQueue;

	static boolean run = true;

	@Resource
	IndigoCustomAdaptor iservice;
	
	@Resource
	CallGenerator callGenerator;
	
	
	public void setLoggingQueue(BlockingQueue<Object> loggingQueue) {
		this.inQueue = loggingQueue;
	}

	public void setServiceCount(int serviceCount) {
		this.serviceCount = serviceCount;
	}

	public void start() {
		executorService = Executors.newFixedThreadPool(serviceCount);

		for (int i = 0; i < serviceCount; i++) {

			executorService.execute(new Runnable() {

				@Override
				public void run() {
					while (run) {
 					    try {
							Object obj = inQueue.take();
							Message msg = callGenerator.generate(obj);
							if(msg == null){
								continue;
							}
							iservice.onSignal(iservice.getInterfaceMap().get(msg.getStringProperty(IndigoHeaderJMSPropertyConstants.ESB_IF_ID)), msg, msg.getStringProperty(IndigoHeaderJMSPropertyConstants.ESB_POLL_TYPE) , obj);
						} catch (Exception e) {
							logger.error("InterfaceCaller Exception : ", e);
						}
					}//end of while
				}//end of run
			});
			logger.info("InterfaceCaller {} ready", (i + 1));
		}
	}

	public void stop() {
		run = false;

		if (!executorService.isShutdown()) {
			executorService.shutdown();
		}
	}

	protected String getMybaitsSqlId(String interfaceId, QueryStatementType insert) {
		StringBuffer sb = new StringBuffer();
		sb.append(interfaceId);
		sb.append(".");
		sb.append(insert);
		return sb.toString();
	}
	
	public interface CallGenerator{
		public Message generate(Object obj) throws Exception;
	}
}
