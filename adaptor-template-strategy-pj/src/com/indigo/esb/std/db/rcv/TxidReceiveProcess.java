package com.indigo.esb.std.db.rcv;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * 사용 DB 수신 처리
 * 
 * @author clupine
 *
 */
public class TxidReceiveProcess extends OnMessageSpacenameDBSupport {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> rowMapList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		Map<String, String> addDataMap = new java.util.HashMap<String, String>();

		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);

		addDataMap.put(dbInterfaceInfo.getTxIdColName(), tx_id);
		addDataMap.put(dbInterfaceInfo.getTimeColName(), DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS"));

		if(dbInterfaceInfo instanceof DBInterfaceInfo && ((DBInterfaceInfo)dbInterfaceInfo).getTargetDeleteTableName()!=null){
			deleteTable(sqlSession, ((DBInterfaceInfo)dbInterfaceInfo).getTargetDeleteTableName());
		}
		
		int insertCnt = 0;

		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.INSERT);
		for (Map<String, Object> dataMap : rowMapList) {
			dataMap.putAll(addDataMap);
			insertCnt += sqlSession.update(getSqlId, dataMap);
		}

		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			log.info("Process Batch Insert Count : " + sqlSession.flushStatements().get(0).getUpdateCounts().length);
		} else {
			log.info("Process Insert Count : " + insertCnt);
		}
	}
	
	/**
	 * 테이블 삭제 처리
	 * @param session
	 * @param tableName
	 * @throws Exception
	 */
	public void deleteTable(SqlSession session, String tableName) throws Exception{
		try {
			if(!session.getConfiguration().getMapperRegistry().hasMapper(QueryMapper.class)){
				session.getConfiguration().addMapper(QueryMapper.class);
			}
			
			QueryMapper mapper = session.getMapper(QueryMapper.class);
			HashMap<String, String> queryMap = new HashMap<String, String>();
			queryMap.put("query", "DELETE " + tableName);
			mapper.delete(queryMap);
			
		}catch(Exception e){
			logger.error("Exception While Delete Table : " + e.getMessage());
			throw e;
		}
	}
}

