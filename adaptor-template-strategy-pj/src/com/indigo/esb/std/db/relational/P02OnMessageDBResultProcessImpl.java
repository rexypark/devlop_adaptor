package com.indigo.esb.std.db.relational;

import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageDBSupport;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.std.db.relational.common.DataSet;
import com.indigo.esb.std.db.relational.common.PLCInterfaceInfo;
import com.indigo.esb.std.db.relational.common.PLCQueryStatementType;
import com.indigo.esb.std.db.relational.common.SqlUtil;

public class P02OnMessageDBResultProcessImpl extends OnMessageDBSupport {

	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		PLCInterfaceInfo interfaceInfo = (PLCInterfaceInfo) indigoMessageResult.getInterfaceInfo();

		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap()
				.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		String time = indigoMessageResult.getProperties().getHeaderInfoMap()
				.get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME);
		String stateCd = indigoMessageResult.getProperties().getHeaderInfoMap()
				.get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS);
		String message = indigoMessageResult.getProperties().getHeaderInfoMap()
				.get(IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG);

		Map<String, String> addDataMap = new java.util.HashMap<String, String>();
		addDataMap.put(interfaceInfo.getTxIdColName(), tx_id);
		addDataMap.put(interfaceInfo.getTimeColName(), time);
		addDataMap.put(interfaceInfo.getStateColName(), stateCd);
		if (message != null)
			addDataMap.put(IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG, message);

		if (interfaceInfo.getMessageColName() != null) {
			if (stateCd.equals("S")) {
				addDataMap.put(interfaceInfo.getMessageColName(), "OK");
			} else {
				int size = message.getBytes().length;
				if (size > 700) {
					message = message.substring(0, 700);
				}
				addDataMap.put(interfaceInfo.getMessageColName(), message);
			}
		}

		DataSet pollData = (DataSet) indigoMessageResult.getDataObj();
		Map<String, Object> mainData = pollData.getMainData();
		mainData.putAll(addDataMap);
		int updateCnt = 0;
		log.debug("결과 업데이트 Parameter:" + mainData);
		String getSqlId = SqlUtil.getMybaitsSqlId(interfaceInfo.getInterfaceId(),
				PLCQueryStatementType.RESULT_UPDATE_MAIN);
		sqlSession.update(getSqlId, mainData);

		log.info("Result Process Update Count : " + updateCnt);

	}

}