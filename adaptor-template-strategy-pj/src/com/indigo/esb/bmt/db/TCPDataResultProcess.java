package com.indigo.esb.bmt.db;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 *  송신측 결과 처리
 * @author clupine
 *
 */
public class TCPDataResultProcess extends OnSignalSpacenameDBSupport {
	
	protected SqlSessionTemplate sqlSession;
	
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}
 
	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();

		Map<String, String> addDataMap = new java.util.HashMap<String, String>();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> resultList = (List<Map<String, Object>>) onSignalResult.getPollResultDataObj();
		
		String flagType = dbInterfaceInfo.getFlagType();
		String patternType = dbInterfaceInfo.getPatternType();
		
		String tx_id = onSignalResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		String time = onSignalResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME);
		String stateCd = onSignalResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS);
		String message = onSignalResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG);
		
		addDataMap.put(dbInterfaceInfo.getTxIdColName(), tx_id);
		addDataMap.put(dbInterfaceInfo.getTimeColName(), time);
		addDataMap.put(dbInterfaceInfo.getStateColName(), stateCd);
		
		if(dbInterfaceInfo.getMessageColName() != null){
			if(stateCd.equals("S")){
				if("Y".equals(flagType))
					addDataMap.put(dbInterfaceInfo.getStateColName(), flagType);
				
				addDataMap.put(dbInterfaceInfo.getMessageColName(), "OK");
			}else{
				if("AS".equals(patternType)){
					addDataMap.put(dbInterfaceInfo.getStateColName(), "E");
					addDataMap.put(dbInterfaceInfo.getMessageColName(), "ERROR");
				}else{
					int size = message.getBytes().length;
					if(size > 255){
						message = message.substring(0,200);
					}
					addDataMap.put(dbInterfaceInfo.getMessageColName(), message);
				}
			}
		}
		int updateCnt = 0;

		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.SOURCE_RESULT_UPDATE);
		log.info("ESB Result Header : " + onSignalResult.getProperties().getHeaderInfoMap());
		//결국 한건
		resultList.get(0).putAll(addDataMap);
		log.info("ESB Result : {} ,  {}" , getSqlId , resultList.get(0));
		updateCnt += sqlSession.update(getSqlId, resultList.get(0));

		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			log.info("Result Process Batch Update Count : "
					+ sqlSession.flushStatements().get(0).getUpdateCounts().length);
		} else {
			log.info("Result Process Update Count : " + updateCnt);
		}

	}

}
