package com.indigo.esb.adaptor.db;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;

public class DefaultOnMessageDBProcessImpl extends OnMessageDBSupport {

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> rowMapList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		Map<String, String> addDataMap = indigoMessageResult.getProperties().getHeaderInfoMap();

		int insertCnt = 0;

		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.INSERT);

		for (Map<String, Object> dataMap : rowMapList) {
			dataMap.putAll(addDataMap);
			insertCnt += sqlSession.update(getSqlId, dataMap);
		}

		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			log.info("Process Batch Insert Count : " + sqlSession.flushStatements().get(0).getUpdateCounts().length);
		} else {
			log.info("Process Insert Count : " + insertCnt);
		}

	}

}
