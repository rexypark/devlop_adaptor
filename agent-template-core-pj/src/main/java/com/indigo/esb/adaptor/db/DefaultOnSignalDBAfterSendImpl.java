package com.indigo.esb.adaptor.db;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;

public class DefaultOnSignalDBAfterSendImpl extends OnSignalDBSupport {

	@SuppressWarnings("unchecked")
	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();

		// property add Data Update
		final Map<String, String> addDataMap = onSignalResult.getProperties().getHeaderInfoMap();
		final List<Map<String, Object>> list = (List<Map<String, Object>>) onSignalResult.getPollResultDataObj();

		int updateCnt = 0;

		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.SOURCE_UPDATE);
		for (Map<String, Object> dataMap : list) {
			dataMap.putAll(addDataMap);
			updateCnt += sqlSession.update(getSqlId, dataMap);
		}

		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			log.info("AfterSend Batch Update Count : " + sqlSession.flushStatements().get(0).getUpdateCounts().length);
		} else {
			log.info("AfterSend Update Count : " + updateCnt);
		}

	}
}
