package com.indigo.esb.std.db.direct;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;

/**
 * 배치성 데이터 원장 Source 테이블을  인터페이스 테이블로.
 * @author clupine 
 */
public class BatchTableProcess extends OnSignalSpacenameDBSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		
		List<Map<String, Object>> pollDataList = null;
		
		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();
		
		pollDataList = sqlSession.selectList(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SELECT));
		
		if(pollDataList != null){
			log.info("Process Select Count : " + pollDataList.size());
		}
		
		Map<String, String> addDataMap = new java.util.HashMap<String, String>();
		
		if(pollDataList == null || pollDataList.size() == 0){
			return;
		}
		

		int insertCnt = 0;

		String getSqlId = getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.INSERT);
		
		for (Map<String, Object> dataMap : pollDataList) {
			dataMap.putAll(addDataMap);
			insertCnt += sqlSession_tgt.update(getSqlId, dataMap);
		}

		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			log.info("Process Batch Insert Count : " + sqlSession_tgt.flushStatements().get(0).getUpdateCounts().length);
		} else {
			log.info("Process Insert Count : " + insertCnt);
		}
		
		if(info.isStoredProcedure()){
			int result =  sqlSession_tgt.update(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.CALL), onSignalResult.getProperties().getHeaderInfoMap());
			log.info("Procedure Call : " + result);
		}
		
		onSignalResult.setResultCount(pollDataList.size());
	}

}
