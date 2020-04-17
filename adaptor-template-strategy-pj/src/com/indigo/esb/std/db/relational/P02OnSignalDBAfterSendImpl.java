package com.indigo.esb.std.db.relational;

import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalDBSupport;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.std.db.relational.common.DataSet;
import com.indigo.esb.std.db.relational.common.PLCInterfaceInfo;
import com.indigo.esb.std.db.relational.common.PLCQueryStatementType;
import com.indigo.esb.std.db.relational.common.SqlUtil;

public class P02OnSignalDBAfterSendImpl extends OnSignalDBSupport {


	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		
		PLCInterfaceInfo interfaceInfo = (PLCInterfaceInfo) onSignalResult.getInterfaceInfo();
		
		int updateCount = 0;
		Map<String, Object> mainData = ((DataSet) onSignalResult.getPollResultDataObj()).getMainData();
		
		String tx_id = onSignalResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		String time = onSignalResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME);
		
		Map<String, String> addDataMap = new java.util.HashMap<String, String>();
		//컬럼값이 기본값이 아닌경우 지정 입력
		if(interfaceInfo.getTxIdColName() != null){
			addDataMap.put(interfaceInfo.getTxIdColName(), tx_id);
		}
		if(interfaceInfo.getTimeColName() != null){
			addDataMap.put(interfaceInfo.getTimeColName(), time);
		}
		
		addDataMap.putAll(onSignalResult.getProperties().getHeaderInfoMap());
		mainData.putAll(addDataMap);
		String getSqlId = SqlUtil.getMybaitsSqlId(interfaceInfo.getInterfaceId(), PLCQueryStatementType.UPDATE_MAIN);
		sqlSession.update(getSqlId, mainData);
		updateCount += 1;
		
		log.debug("Update Count : {} ", updateCount);

	}


}
