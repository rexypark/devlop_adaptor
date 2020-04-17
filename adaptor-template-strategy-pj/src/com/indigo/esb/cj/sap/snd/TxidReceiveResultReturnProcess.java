package com.indigo.esb.cj.sap.snd;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;


/**
 *  송신측 결과 처리
 *  TO-BE DB2SAP 패턴중 RFC 호출 결과를 받아서 송신측 DB에 행별로 반영함, 즉 Transaction 단위(ex. 1000건)로 update하지 않음.
 *  @author clupine
 *
 */
public class TxidReceiveResultReturnProcess extends OnMessageSpacenameDBSupport {
 
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> resultList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		
		int updateCnt = 0;
		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.SOURCE_RESULT_UPDATE);

		for (Map<String, Object> dataMap : resultList) {
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