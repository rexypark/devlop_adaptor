package com.indigo.esb.std.db.relational;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalDBSupport;
import com.indigo.esb.std.db.relational.common.DataSet;
import com.indigo.esb.std.db.relational.common.PLCInterfaceInfo;
import com.indigo.esb.std.db.relational.common.PLCQueryStatementType;
import com.indigo.esb.std.db.relational.common.SqlUtil;

public class P02OnSignalDBPollImpl extends OnSignalDBSupport {

	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		PLCInterfaceInfo interfaceInfo = (PLCInterfaceInfo) onSignalResult.getInterfaceInfo();
		String getSqlId = "";
		DataSet dataSet = new DataSet();
		Map<String, Object> mainResult = null;
		List<Map<String, Object>> detailResult = null;
		Map<String, Object> resultData = new LinkedHashMap<String, Object>();
		String[] detailTableList = null;

		if (interfaceInfo.getSubTableName() != null || "".equalsIgnoreCase(interfaceInfo.getSubTableName())) {
			detailTableList = interfaceInfo.getSubTableName().split(",");
		}

		getSqlId = SqlUtil.getMybaitsSqlId(interfaceInfo.getInterfaceId(), PLCQueryStatementType.SELECT_MAIN);
		mainResult = sqlSession.selectOne(getSqlId);
		if (mainResult == null) {
			onSignalResult.addProperty(ESB_SEND_ROW_COUNT, String.valueOf(0));
			return;
		}

		dataSet.setMainData(mainResult);

		if (mainResult == null)
			throw new Exception("잘못된 데이터 메인 정보입니다.");

		if (interfaceInfo.getSubTableName() != null || !"".equalsIgnoreCase(interfaceInfo.getSubTableName())) {
			for (int x = 0; x < detailTableList.length; x++) {
				getSqlId = SqlUtil.getSubTableSqlId(interfaceInfo.getInterfaceId(), detailTableList[x], PLCQueryStatementType.SELECT_DETAIL);
				detailResult = sqlSession.selectList(getSqlId, mainResult);
				resultData.put(detailTableList[x] ,detailResult);
			}
			dataSet.setDetailList(resultData);
		}
		onSignalResult.setPollResultDataObj(dataSet);
		onSignalResult.addProperty(ESB_SEND_ROW_COUNT, "" + dataSet.getDetailList().size());
	}
}
