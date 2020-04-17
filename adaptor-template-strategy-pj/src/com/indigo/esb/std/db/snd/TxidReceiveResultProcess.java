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
 *  송신측 결과 처리
 * @author clupine
 *
 */
public class TxidReceiveResultProcess extends OnMessageSpacenameDBSupport {
 
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();

		Map<String, String> addDataMap = new java.util.HashMap<String, String>();

		@SuppressWarnings("unchecked")
		Object obj = (Object) indigoMessageResult.getDataObj();
		List<Map<String, Object>> resultList;
		
		if(obj instanceof Map){
			resultList = new ArrayList<Map<String,Object>>(); 
			resultList.add((Map<String, Object>) obj);
		}else{
			resultList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		}
		
		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		String time = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME);
		String stateCd = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS);
		String message = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG);
		
		addDataMap.put(dbInterfaceInfo.getTxIdColName(), tx_id);
		addDataMap.put(dbInterfaceInfo.getTimeColName(), time);
		addDataMap.put(dbInterfaceInfo.getStateColName(), stateCd);
		
		if(dbInterfaceInfo.getMessageColName() != null){
			if(stateCd.equals("S")){
				addDataMap.put(dbInterfaceInfo.getMessageColName(), "OK");
			}else{
					int size = message.getBytes().length;
					if(size > 255){
						message = message.substring(0,200);
					}
					addDataMap.put(dbInterfaceInfo.getMessageColName(), message);
			}
		}
		int updateCnt = 0;

		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.SOURCE_RESULT_UPDATE);
		log.info("ESB Result Header : " + indigoMessageResult.getProperties().getHeaderInfoMap());
		//결국 한건
		for (Map<String, Object> dataMap : resultList) {
			dataMap.putAll(addDataMap);
			//log.info("result data : " + dataMap.toString());
			log.info("ESB Result : {} ,  {}" , getSqlId , dataMap);
			updateCnt += sqlSession.update(getSqlId, dataMap);
			//sqlSession.commit(true);
		}

		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			log.info("Result Process Batch Update Count : "
					+ sqlSession.flushStatements().get(0).getUpdateCounts().length);
		} else {
			log.info("Result Process Update Count : " + updateCnt);
		}

	}

}
