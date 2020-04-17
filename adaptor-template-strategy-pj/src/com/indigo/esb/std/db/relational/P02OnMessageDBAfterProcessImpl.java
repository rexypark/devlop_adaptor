package com.indigo.esb.std.db.relational;

import java.util.HashMap;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageDBSupport;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.std.db.relational.common.DataSet;
import com.indigo.esb.std.db.relational.common.PLCInterfaceInfo;

public class P02OnMessageDBAfterProcessImpl extends OnMessageDBSupport {

	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		PLCInterfaceInfo interfaceInfo = (PLCInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		
		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		String time = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME);
		
		Map<String, String> addDataMap = new java.util.HashMap<String, String>();
		//컬럼값이 기본값이 아닌경우 지정 입력
		if(interfaceInfo.getTxIdColName() != null){
			addDataMap.put(interfaceInfo.getTxIdColName(), tx_id);
		}
		if(interfaceInfo.getTimeColName() != null){
			addDataMap.put(interfaceInfo.getTimeColName(), time);
		}
		
		addDataMap.putAll(indigoMessageResult.getProperties().getHeaderInfoMap());

		DataSet msgDS = 	(DataSet) indigoMessageResult.getDataObj();
		String pkVal = (String)msgDS.getMainData().get("DEVDNC_PACK_ID");
		
		Map<String, Object> mainData = new HashMap<String,Object>();
		mainData.putAll(addDataMap);
		mainData.put("DEVDNC_PACK_ID", pkVal);
		DataSet resultDataSet = new DataSet(); 
		resultDataSet.setMainData(mainData);
		log.debug("처리결과 응답:"+resultDataSet);
		this.jmsTemplate.convertAndSend(indigoMessageResult.getInterfaceInfo().getTargetDestinationName(),
				indigoMessageResult.getTemplateMessage(resultDataSet), indigoMessageResult.getProperties());
	}
}
