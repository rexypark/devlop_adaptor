package com.indigo.esb.std.db.rcv;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_FAIL;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.OnMessageStrategy;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * 디비 성공 결과 JMS 결과 리턴 TXID
 * TX_ID만을 결과로 리턴한다. 
 * 
 * Interface 설정에서 지정한 컬럼들도 리턴하려면 com.indigo.esb.adaptor.db.DefaultOnMessageDBAfterProcessImpl 를사용하시오
 * @author clupine
 *
 */
public class TxidReceiveResultToJMS implements OnMessageStrategy{

	@Autowired(required = false)
	protected JmsTemplate jmsTemplate;
	
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> recvList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		Map<String , Object> oneDataMap;
		if(recvList.size() == 0){
			indigoMessageResult.setPocessStatus(ESB_TRANS_FAIL);
			indigoMessageResult.addProperty(ESB_ERR_MSG, "data size is zero");
			oneDataMap = new HashMap<String, Object>();
		}else{
			oneDataMap = recvList.get(0);
		}
		
		
		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		String stateCd = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS);
		
		//return
		List<Map<String , Object>> resultDataList = new ArrayList<Map<String , Object>>();
		HashMap<String,Object> resultDataMap = new HashMap<String,Object>();
		resultDataMap.put(dbInterfaceInfo.getStateColName(), stateCd);
		resultDataMap.put(dbInterfaceInfo.getTxIdColName(),  tx_id );
		resultDataMap.put(dbInterfaceInfo.getTimeColName(),  oneDataMap.get(dbInterfaceInfo.getTimeColName()));
		resultDataList.add(resultDataMap);
		
		this.jmsTemplate.convertAndSend(indigoMessageResult.getInterfaceInfo().getTargetDestinationName(),
				indigoMessageResult.getTemplateMessage(resultDataList), indigoMessageResult.getProperties());
	}

}
