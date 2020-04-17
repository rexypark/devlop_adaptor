package com.indigo.esb.std.db.snd;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;

/**
 * Table Select 
 * @author clupine 
 *
 */
public class TxidSendPollingData extends OnSignalSpacenameDBSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		
		@SuppressWarnings("rawtypes")
		Map headerMap = onSignalResult.getProperties().getHeaderInfoMap();
		
		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();
		
		List<Map<String, Object>> pollDataList = sqlSession.selectList(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SELECT ) , headerMap);

		onSignalResult.setPollResultDataObj(pollDataList);
		if (pollDataList != null) {
			onSignalResult.setResultCount(pollDataList.size());
			log.info("onSinalResult : "  + onSignalResult.getTemplateMessage());
		}
	}

}
