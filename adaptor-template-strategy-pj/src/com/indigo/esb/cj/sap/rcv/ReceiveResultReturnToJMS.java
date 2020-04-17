package com.indigo.esb.cj.sap.rcv;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;

/**
 * 디비 성공 결과 JMS 결과 리턴 TXID
 *  TO-BE DB2SAP 패턴에서 IF 결과를 행별로 송신DB에 반영하기 위해 List<Map>에서 Map 객체별로 EAI 컬럼값들 세팅
 * @author clupine
 *
 */
public class ReceiveResultReturnToJMS extends OnMessageSpacenameDBSupport {

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> recvList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		
		for(Map<String, Object> map : recvList){
			map.put(dbInterfaceInfo.getMessageColName(),  "OK");
			map.put(dbInterfaceInfo.getStateColName(), "S");
			map.put(dbInterfaceInfo.getTimeColName(),  DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS"));
		}
		
		this.jmsTemplate.convertAndSend(indigoMessageResult.getInterfaceInfo().getTargetDestinationName(),
				indigoMessageResult.getTemplateMessage(recvList), indigoMessageResult.getProperties());
	}
}