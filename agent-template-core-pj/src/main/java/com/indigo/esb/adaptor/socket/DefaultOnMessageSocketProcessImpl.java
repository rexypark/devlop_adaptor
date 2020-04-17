package com.indigo.esb.adaptor.socket;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.socket.OnMessageSocketSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;

public class DefaultOnMessageSocketProcessImpl extends OnMessageSocketSupport  {

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> rowMapList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		Map<String, String> addDataMap = indigoMessageResult.getProperties().getHeaderInfoMap();
		for (Map<String, Object> map : rowMapList) {
			
		}
		int insertCnt = 0;

	}

}
