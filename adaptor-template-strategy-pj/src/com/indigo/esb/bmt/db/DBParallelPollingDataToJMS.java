package com.indigo.esb.bmt.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * Table Select 
 * @author clupine 
 *
 */
public class DBParallelPollingDataToJMS extends OnSignalSpacenameDBSupport {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	ExecutorService executorService;

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		
		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();
		
		executorService = Executors.newFixedThreadPool(info.getParallelCount());
		
		int sendRowCount = info.getSendRowCount();
		int fixTotalCount = info.getFixTotalCount();
		
		int addCount =  (int) Math.ceil((double)fixTotalCount/sendRowCount);
		logger.info("======= total send count : " + addCount);
		List<Future<Integer>> transferResultList = new ArrayList<Future<Integer>>();
		
		int count = 0;
		
		for (int i = 0; i < addCount; i++) {
//			transferResultList.add(executorService.submit(new DBtoJMSCallable(count+1,sendRowCount+count,new IndigoSignalResult(info), sqlSession , jmsTemplate)));
			transferResultList.add(executorService.submit(new DBtoJMSCallable(count+1,sendRowCount+count,info, sqlSession , jmsTemplate)));
			count += sendRowCount;
		}
		
		long totalCount = 0;
		
		for (Future<Integer> future : transferResultList) {
			Integer result = null;
//			try{
//				result  = future.get(20 , TimeUnit.SECONDS);
//			}catch(Exception e){
//				result = null;
//			}
			
			while(result == null){
				try{
					result  = future.get(20 , TimeUnit.SECONDS);
				}catch(Exception e){
					result = null;
				}
			}
			
			if(result!=null){
				totalCount+=result;
			}
		}
			logger.info("totalCount : " + totalCount);
			onSignalResult.addProperty(IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT, ""+totalCount);
			
		executorService.shutdownNow();
	}
}

class DBtoJMSCallable implements Callable<Integer>{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	int startRow;
	int endRow;
	protected SqlSessionTemplate sqlSession;
	JmsTemplate jmsTemplate;
	DBInterfaceInfo info;
	
	public DBtoJMSCallable(int startRow , int endRow , DBInterfaceInfo info , SqlSessionTemplate sqlSession  ,JmsTemplate jmsTemplate ) {
		this.startRow = startRow;
		this.endRow = endRow;
		this.info = info;
		this.sqlSession = sqlSession;
		this.jmsTemplate = jmsTemplate;
	}
	
	@Override
	public Integer call() throws Exception {
		IndigoSignalResult onSignalResult = new IndigoSignalResult(info);
		Map headerMap = onSignalResult.getProperties().getHeaderInfoMap();
		headerMap.put("START_NUM", startRow+"");
		headerMap.put("END_NUM", endRow+"");
		logger.info("SQL Start : startRow : " + startRow + " , endRow : "+ endRow);
		List<Map<String, Object>> pollDataList = sqlSession.selectList(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SELECT ) , headerMap);
		onSignalResult.setPollResultDataObj(pollDataList);
		if (pollDataList != null) {
			if(pollDataList.size()==0){
				return 0;
			}
			
			onSignalResult.setResultCount(pollDataList.size());
			String sendDestinationName = info.getTargetDestinationName();
			
			if(info.isNoLogSendMsgbool()){
				onSignalResult.addProperty("NO_LOG_SND_MSG", "Y");
			}
			logger.info("SQL End : startRow : " + startRow + " , endRow : "+ endRow);
			this.jmsTemplate.convertAndSend(sendDestinationName, onSignalResult.getTemplateMessage(),
					onSignalResult.getProperties());
			logger.info("startRow : " + startRow + " , endRow : "+ endRow + "JMS Send Complete");
		}
		
		return pollDataList.size();
	}
	
	protected String getMybaitsSqlId(String interfaceId, QueryStatementType insert) {
		StringBuffer sb = new StringBuffer();
		sb.append(interfaceId);
		sb.append(".");
		sb.append(insert);
		return sb.toString();
	}
}