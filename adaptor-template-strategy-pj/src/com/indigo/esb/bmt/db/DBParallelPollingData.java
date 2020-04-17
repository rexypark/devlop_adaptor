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
public class DBParallelPollingData extends OnSignalSpacenameDBSupport {
	
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
		List<Future<IndigoSignalResult>> transferResultList = new ArrayList<Future<IndigoSignalResult>>();
		
		int count = 0;
		
		TCPDataTransferProcess transferProcess = new TCPDataTransferProcess(info);
		
		for (int i = 0; i < addCount; i++) {
			transferResultList.add(executorService.submit(new DBTransferCallable(count+1,sendRowCount+count,new IndigoSignalResult(info), sqlSession,transferProcess)));
			count += sendRowCount;
		}
		
		long totalCount = 0;
		
		for (Future<IndigoSignalResult> future : transferResultList) {
			IndigoSignalResult result = null;
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
				totalCount+=Integer.valueOf(result.getProperty(IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT));
			}
		}
			logger.info("totalCount : " + totalCount);
			onSignalResult.addProperty(IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT, ""+totalCount);
			
		executorService.shutdownNow();
	}
}

class DBTransferCallable implements Callable<IndigoSignalResult>{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	int startRow;
	int endRow;
	IndigoSignalResult onSignalResult;
	protected SqlSessionTemplate sqlSession;
	static TCPDataTransferProcess transferProcess;
	
	public DBTransferCallable(int startRow , int endRow , IndigoSignalResult onSignalResult , SqlSessionTemplate sqlSession, TCPDataTransferProcess transferProcess) {
		this.startRow = startRow;
		this.endRow = endRow;
		this.onSignalResult = onSignalResult;
		this.sqlSession = sqlSession;
		this.transferProcess = transferProcess;
	}
	
	@Override
	public IndigoSignalResult call() throws Exception {
		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();
		Map headerMap = onSignalResult.getProperties().getHeaderInfoMap();
		headerMap.put("START_NUM", startRow+"");
		headerMap.put("END_NUM", endRow+"");
		
		List<Map<String, Object>> pollDataList = sqlSession.selectList(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SELECT ) , headerMap);
		onSignalResult.setPollResultDataObj(pollDataList);
		if (pollDataList != null) {
			onSignalResult.setResultCount(pollDataList.size());
		}
		transferProcess.onStart(onSignalResult);
		
		return onSignalResult;
	}
	
	protected String getMybaitsSqlId(String interfaceId, QueryStatementType insert) {
		StringBuffer sb = new StringBuffer();
		sb.append(interfaceId);
		sb.append(".");
		sb.append(insert);
		return sb.toString();
	}
}