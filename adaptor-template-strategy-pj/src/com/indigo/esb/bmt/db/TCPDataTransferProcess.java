package com.indigo.esb.bmt.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.OnSignalStrategy;
import com.indigo.esb.bmt.tcp.client.ObjectSyncClient;
import com.indigo.esb.bmt.tcp.message.TransferObject;
import com.indigo.esb.config.DBInterfaceInfo;
import com.mb.mci.common.exception.ConnectionFailException;

/**
 * 데이터 JMS 송신
 * @author clupine
 *
 */
public class TCPDataTransferProcess implements OnSignalStrategy {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	ObjectSyncClient<TransferObject> client ;
	public TCPDataTransferProcess(DBInterfaceInfo info) {
		try {
			client = new ObjectSyncClient<TransferObject>(info.getIp(),info.getPort());
		} catch (ConnectionFailException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();
		TransferObject reqObj = new TransferObject();
		reqObj.setData(onSignalResult.getPollResultDataObj());
		reqObj.setHeaderMap(onSignalResult.getProperties().getHeaderInfoMap());
		client.send(reqObj );
//		if(resObj.getResultMessage()==null){
//			onSignalResult.getProperties().addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS,IndigoHeaderJMSPropertyConstants.ESB_TRANS_SUCCESS);
//			logger.info("처리 성공 : " + resObj.getResultMessage());
//		}else{
//			onSignalResult.getProperties().addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS,IndigoHeaderJMSPropertyConstants.ESB_TRANS_FAIL);
//			onSignalResult.getProperties().addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG,resObj.getResultMessage());
//			logger.info("처리 실패 : " + resObj.getResultMessage());
//		}
	}
}
