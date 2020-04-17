package com.indigo.esb.bmt.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.indigo.esb.bmt.tcp.message.TransferObject;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.config.InterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

public class DBInsertService implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(DBInsertService.class);

	protected ExecutorService executorService;
	protected int serviceCount = 1;

	@Resource
	BlockingQueue<Object> inQueue;

	static boolean run = true;

	@Resource
	SqlSessionFactory sqlSessionFactory;

	@Resource
	PlatformTransactionManager transactionManager;

	@Resource
	List<Object> interfaceList;

	Map<String, InterfaceInfo> interfaceMap;

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

				SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
				TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

				@Override
				public void run() {
					while (run) {
						try {
							TransferObject tfobj = (TransferObject) inQueue.take();

							final DBInterfaceInfo info = (DBInterfaceInfo) interfaceMap.get(tfobj.getHeaderMap().get(
									IndigoHeaderJMSPropertyConstants.ESB_IF_ID));

							final List<Map<String, Object>> rowMapList = (List<Map<String, Object>>) tfobj.getData();
							
							transactionTemplate.execute(new TransactionCallbackWithoutResult() {
								protected void doInTransactionWithoutResult(TransactionStatus status) {
									try {

										int insertCnt = 0;
										String getSqlId = getMybaitsSqlId(info.getInterfaceId(),
												QueryStatementType.INSERT);
										for (Map<String, Object> dataMap : rowMapList) {
											insertCnt += sqlSessionTemplate.update(getSqlId, dataMap);
										}
										if (sqlSessionTemplate.getExecutorType() == ExecutorType.BATCH) {
											sqlSessionTemplate.flushStatements();
											logger.info("Process Batch Insert Count : " + rowMapList.size());
										} else {
											logger.info("Process Insert Count : " + insertCnt);
										}
									} catch (Exception ex) {
										status.setRollbackOnly();
									}
								}
							});//end of transactionTemplate

						} catch (Exception e) {
							logger.error("DBInsertService Exception : ", e);
						}

					}//end of while
				}//end of run
			});
			logger.info("DBInsertService {} ready", (i + 1));
		}
	}

	public void stop() {
		run = false;

		if (!executorService.isShutdown()) {
			executorService.shutdown();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		interfaceMap = new HashMap<String, InterfaceInfo>();
		for (Object info : interfaceList) {
			if (info instanceof InterfaceInfo) {
				interfaceMap.put(((InterfaceInfo) info).getInterfaceId(), (InterfaceInfo) info);
			}
		}
	}

	protected String getMybaitsSqlId(String interfaceId, QueryStatementType insert) {
		StringBuffer sb = new StringBuffer();
		sb.append(interfaceId);
		sb.append(".");
		sb.append(insert);
		return sb.toString();
	}

}
