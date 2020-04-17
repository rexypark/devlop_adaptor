package com.indigo.esb.std.hadoop.rcv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.HiveInterfaceInfo;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * 디비 성공 결과 JMS 결과 리턴 TXID
 * @author clupine
 *
 */
public class TxidReceiveResultToJMS extends OnMessageSpacenameDBSupport {

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> recvList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		HiveInterfaceInfo interfaceInfo = (HiveInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		Map<String , Object> oneDataMap = recvList.get(0);
		
		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		String stateCd = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS);
		
		//return
		List<Map<String , Object>> resultDataList = new ArrayList<Map<String , Object>>();
		HashMap<String,Object> resultDataMap = new HashMap<String,Object>();
		resultDataMap.put(interfaceInfo.getStateColName(), stateCd);
		resultDataMap.put(interfaceInfo.getTxIdColName(),  tx_id );
		resultDataMap.put(interfaceInfo.getTimeColName(),  oneDataMap.get(interfaceInfo.getTimeColName()));
		resultDataList.add(resultDataMap);
		
		this.jmsTemplate.convertAndSend(indigoMessageResult.getInterfaceInfo().getTargetDestinationName(),
				indigoMessageResult.getTemplateMessage(resultDataList), indigoMessageResult.getProperties());
	}

}
