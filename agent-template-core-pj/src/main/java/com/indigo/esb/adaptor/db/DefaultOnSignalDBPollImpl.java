package com.indigo.esb.adaptor.db;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT;

import java.util.List;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;

public class DefaultOnSignalDBPollImpl extends OnSignalDBSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();

		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.SELECT);

		List<Map<String, Object>> result = sqlSession.selectList(getSqlId);

		onSignalResult.setPollResultDataObj(result);

		if (result != null)
			onSignalResult.addProperty(ESB_SEND_ROW_COUNT, "" + result.size());
	}

}
