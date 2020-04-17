package com.indigo.esb.adaptor.db;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;

public class DefaultOnMessageDBResultProcessImpl extends OnMessageDBSupport {

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();

		Map<String, String> addDataMap = indigoMessageResult.getProperties().getHeaderInfoMap();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> resultList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();

		int updateCnt = 0;

		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.SOURCE_RESULT_UPDATE);

		for (Map<String, Object> dataMap : resultList) {
			dataMap.putAll(addDataMap);
			updateCnt += sqlSession.update(getSqlId, dataMap);
		}

		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			log.info("Result Process Batch Update Count : "
					+ sqlSession.flushStatements().get(0).getUpdateCounts().length);
		} else {
			log.info("Result Process Update Count : " + updateCnt);
		}

	}

}
