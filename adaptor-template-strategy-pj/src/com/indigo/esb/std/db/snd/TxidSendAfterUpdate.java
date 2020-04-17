package com.indigo.esb.std.db.snd;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_SUCCESS;

import java.util.Map;

import org.apache.ibatis.session.ExecutorType;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * 송신처리후 결과 처리
 * 
 * @author clupine
 *
 */
public class TxidSendAfterUpdate extends OnSignalSpacenameDBSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		if (onSignalResult.getProperty(ESB_TRANS_STATUS).equals(ESB_TRANS_SUCCESS)) {
			return;
		}

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();

		Map<String, String> dataMap = new java.util.HashMap<String, String>();

		String tx_id = onSignalResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		String time = onSignalResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME);

		// 컬럼값이 기본값이 아닌경우 지정 입력
		if (dbInterfaceInfo.getTxIdColName() != null) {
			dataMap.put(dbInterfaceInfo.getTxIdColName(), tx_id);
		}
		if (dbInterfaceInfo.getTimeColName() != null) {
			dataMap.put(dbInterfaceInfo.getTimeColName(), time);
		}

		dataMap.putAll(onSignalResult.getProperties().getHeaderInfoMap());

		log.info("ESB Column Value : {} ", dataMap.toString());

		int updateCnt = 0;

		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.SOURCE_UPDATE);
		log.debug("Last Result data : " + dataMap.toString());
		updateCnt += sqlSession.update(getSqlId, dataMap);

		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			log.info("TxidSendAfterUpdate Batch Update Count : " + sqlSession.flushStatements().get(0).getUpdateCounts().length);
		} else {
			log.info("TxidSendAfterUpdate Update Count : " + updateCnt);
		}

	}
}
