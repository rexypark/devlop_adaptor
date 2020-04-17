package com.indigo.esb.std.db.relational;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageDBSupport;
import com.indigo.esb.std.db.relational.common.DataSet;
import com.indigo.esb.std.db.relational.common.PLCInterfaceInfo;
import com.indigo.esb.std.db.relational.common.PLCQueryStatementType;
import com.indigo.esb.std.db.relational.common.SqlUtil;

public class P02OnMessageDBProcessImpl extends OnMessageDBSupport {

	@SuppressWarnings("unchecked")
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		PLCInterfaceInfo interfaceInfo = (PLCInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		String getSqlId = null;
		int mainInsertCnt = 0;
		int detailInsertCnt = 0;
		try {
			DataSet pollData = (DataSet) indigoMessageResult.getDataObj();
			Map<String, Object> mainData = pollData.getMainData();
			mainData.putAll(indigoMessageResult.getProperties().getHeaderInfoMap());

			getSqlId = SqlUtil.getMybaitsSqlId(interfaceInfo.getInterfaceId(), PLCQueryStatementType.INSERT_MAIN);
			sqlSession.insert(getSqlId, mainData);
			mainInsertCnt++;

			Map<String, Object> detailMap = pollData.getDetailList();
			for (Entry<String, Object> entryMap : detailMap.entrySet()) {
				getSqlId = SqlUtil.getSubTableSqlId(interfaceInfo.getInterfaceId(), entryMap.getKey(), PLCQueryStatementType.INSERT_DETAIL);
				List<Map<String, Object>> detailRowList = (List<Map<String, Object>>) entryMap.getValue();
				for (Map<String, Object> detailRow : detailRowList) {
					sqlSession.insert(getSqlId, detailRow);
					detailInsertCnt++;
				}
			}

			log.info("Detail Data Insert Success");
			log.info("Process Insert Count Master =  " + mainInsertCnt + " Detail = " + detailInsertCnt);

		} catch (Exception e) {
			log.error("ERROR : ", e);
			throw e;
			
		}

	}

}
