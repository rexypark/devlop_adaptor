package com.indigo.esb.std.db.rcv;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_FAIL;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.OnMessageStrategy;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.config.InterfaceInfo;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * 디비 성공 결과 JMS 결과 리턴 TXID
 * @author clupine
 *
 */
public class TxidReceiveResultToJMS2 implements OnMessageStrategy{

	@Autowired(required = false)
	protected JmsTemplate jmsTemplate;
	
	Set<String> tgtDisconnflagCodeSet;

	String msgFileBackupPath;
	
	public void setTgtDisconnflagCodeSet(Set<String> tgtDisconnflagCodeSet) {
		this.tgtDisconnflagCodeSet = tgtDisconnflagCodeSet;
	}
	
	public void setMsgFileBackupPath(String msgFileBackupPath) {
		this.msgFileBackupPath = msgFileBackupPath;
	}
	
	private void fileDelete(String fileName) {
		//파일 지우기
		File backupFile = new File(fileName);
		try {
			FileUtils.forceDelete(backupFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		
		InterfaceInfo info = indigoMessageResult.getInterfaceInfo();
		
		String fileName = msgFileBackupPath + File.separator + info.getInterfaceId() + "_"+ indigoMessageResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		if(msgFileBackupPath != null && indigoMessageResult.getPocessStatus().equals(ESB_TRANS_FAIL) && tgtDisconnflagCodeSet != null){
			String errMsg = indigoMessageResult.getProperty(ESB_ERR_MSG);
			for (String errflagCode : tgtDisconnflagCodeSet) {
				if(errMsg.indexOf(errflagCode) > -1){
					return;
				}
			}
		}
		
		fileDelete(fileName); //정상조건
		
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
