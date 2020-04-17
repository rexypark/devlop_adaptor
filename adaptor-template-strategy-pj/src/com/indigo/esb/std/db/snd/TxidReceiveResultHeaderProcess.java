package com.indigo.esb.std.db.snd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;


/**
 *  헤더만 가지고 결과 처리
 * @author clupine
 *
 */
public class TxidReceiveResultHeaderProcess extends OnMessageSpacenameDBSupport {
 
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		Map headerMap = indigoMessageResult.getProperties().getHeaderInfoMap();
		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		String time = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME);
		String stateCd = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS);
		String message = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG);
		
		headerMap.put(dbInterfaceInfo.getTxIdColName(), tx_id);
		headerMap.put(dbInterfaceInfo.getTimeColName(), time);
		headerMap.put(dbInterfaceInfo.getStateColName(), stateCd);
		
		if(dbInterfaceInfo.getMessageColName() != null){
					int size = message.getBytes().length;
					if(size > 255){
						message = message.substring(0,200);
					}
					headerMap.put(dbInterfaceInfo.getMessageColName(), message);
		}
		int updateCnt = 0;

		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.SOURCE_RESULT_UPDATE);
		log.info("ESB Result : {} ,  {}" , getSqlId , headerMap);
		updateCnt += sqlSession.update(getSqlId, headerMap);

		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			log.info("Result Process Batch Update Count : "
					+ sqlSession.flushStatements().get(0).getUpdateCounts().length);
		} else {
			log.info("Result Process Update Count : " + updateCnt);
		}

	}

}
