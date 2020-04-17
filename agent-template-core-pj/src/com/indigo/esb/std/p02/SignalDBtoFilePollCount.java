package com.indigo.esb.std.p02;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

public class SignalDBtoFilePollCount extends OnSignalDBSupport {

	public String path = "../DB_FILE";
	public String serverInfo = "127.0.0.1:24212";
	public String spitChar = "|";

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		List<Map<String, Object>> pollDataList = null;
		Map<String, String> onSignalValueMap = onSignalResult.getInterfaceInfo().getAddDataMap();
		boolean remoteFlag = onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE).equals("REMOTE");
		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();
		String TX_ID = onSignalResult.getProperty(ESB_TX_ID);
		String sql = info.getInterfaceId()+ "_" + "SELECT_COUNT";
		log.info("sql :" + sql);
		int resultCount = sqlSession.selectOne(sql);
		log.info("resultCount : " + resultCount);
		if(resultCount == 0 ){
			onSignalResult.setResultCount(0);
			if(remoteFlag){
				throw new Exception("select count 0");
			}
			return;
		}
		onSignalResult.getInterfaceInfo().getAddDataMap().put("TX_ID", TX_ID);
		onSignalValueMap = onSignalResult.getInterfaceInfo().getAddDataMap();
		resultCount = sqlSession.update(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SOURCE_UPDATE), onSignalValueMap);
		onSignalResult.setResultCount(resultCount);
	}

}
