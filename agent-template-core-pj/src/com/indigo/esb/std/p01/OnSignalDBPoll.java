package com.indigo.esb.std.p01;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE;

import java.util.List;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;

public class OnSignalDBPoll extends OnSignalDBSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		List<Map<String, Object>> pollDataList = null;
		Map<String, String> onSignalValueMap = onSignalResult.getInterfaceInfo().getAddDataMap();
		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();

		if (onSignalValueMap.get(ESB_IF_DATA_TYPE) != null) {
			log.info("#### Remote Message Map : " + onSignalValueMap);
			pollDataList = sqlSession.selectList(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SELECT),
					onSignalValueMap);
		} else {
			pollDataList = sqlSession.selectList(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SELECT));

		}
		onSignalResult.setPollResultDataObj(pollDataList);
		if (pollDataList != null) {
			onSignalResult.setResultCount(pollDataList.size());
			log.info("onSinalResult : "  + onSignalResult.getTemplateMessage());
		}
		
	}

}
