package com.indigo.esb.std.db.snd;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * 디비 송신후 연계상태 처리 
 * @author clupine
 *
 */
public class EachTxidSendDataUpdate extends OnSignalSpacenameDBSupport {

	@SuppressWarnings("unchecked")
	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();

		// property add Data Update
//		final Map<String, String> addDataMap = onSignalResult.getProperties().getHeaderInfoMap();
		Map<String, String> addDataMap = new java.util.HashMap<String, String>();
		
		final List<Map<String, Object>> list = (List<Map<String, Object>>) onSignalResult.getPollResultDataObj();
		
		String tx_id = onSignalResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		String time = onSignalResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME);
		
		//컬럼값이 기본값이 아닌경우 지정 입력
		if(dbInterfaceInfo.getTxIdColName() != null){
			addDataMap.put(dbInterfaceInfo.getTxIdColName(), tx_id);
		}
		
		if(dbInterfaceInfo.getTimeColName() != null){
			addDataMap.put(dbInterfaceInfo.getTimeColName(), time);
		}
		
		addDataMap.putAll(onSignalResult.getProperties().getHeaderInfoMap());
		
		log.info("ESB Column Value : {} " , addDataMap.toString());
		
		int updateCnt = 0;

		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.SOURCE_UPDATE);
		for (Map<String, Object> dataMap : list) {
			dataMap.putAll(addDataMap);
			dataMap.put(IndigoHeaderJMSPropertyConstants.ESB_TX_ID, dbInterfaceInfo.getTxidGenerator().create());
			log.debug("data : " + dataMap.toString());
			updateCnt += sqlSession.update(getSqlId, dataMap);
		}

		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			log.info("AfterSend Batch Update Count : " + sqlSession.flushStatements().get(0).getUpdateCounts().length);
		} else {
			log.info("AfterSend Update Count : " + updateCnt);
		}

	}
}
