package com.indigo.esb.cj.sap.rcv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * 디비 성공 결과 JMS 결과 리턴 TXID
 * @author clupine
 *
 */
public class ReceiveResultToJMS extends OnMessageSpacenameDBSupport {

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> recvList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
//		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();
//		Map<String , Object> oneDataMap = recvList.get(0);
//		
//		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
//		String stateCd = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS);
		
		//return
//		List<Map<String , Object>> resultDataList = new ArrayList<Map<String , Object>>();
//		HashMap<String,Object> resultDataMap = new HashMap<String,Object>();
//		resultDataMap.put(dbInterfaceInfo.getStateColName(), stateCd);
//		resultDataMap.put(dbInterfaceInfo.getTxIdColName(),  tx_id );
//		resultDataMap.put(dbInterfaceInfo.getTimeColName(),  oneDataMap.get(dbInterfaceInfo.getTimeColName()));
//		resultDataList.add(resultDataMap);

		log.debug("recvListt.toString()======================================================>"+recvList.toString());
//		@SuppressWarnings("unchecked")
//		List<Map<String, Object>> recvList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
//		
		this.jmsTemplate.convertAndSend(indigoMessageResult.getInterfaceInfo().getTargetDestinationName(),
				indigoMessageResult.getTemplateMessage(recvList), indigoMessageResult.getProperties());		
	}
}
