package com.indigo.esb.std.db.snd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;
import com.indigo.esb.config.CJConstants;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * DB2DB 방식.
 * 
 * 조회 데이터가 있을 경우 Update 후 Select 처리 (기존은 Select 후 Update 처리함)
 * - 조회 테이블에 PK가 필요 없음
 * 
 * @author clupine 
 */
public class CJSendPollingDataBasic extends OnSignalSpacenameDBSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		List<Map<String, Object>> pollDataList = null;
		HashMap<String, Object> selectParamMap = new HashMap<String, Object>();
		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();
		String tx_id = null;

		// IF 대상 데이터가 있는 지 확인
		int ifRowsNum = sqlSession.selectOne(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.CHECK));
		
		log.info("ifRowsNum : "  + ifRowsNum);
		
		try{
			// 데이터가 있으면 TX_ID를 생성하여 상태값과 함께 Update하고 데이터 조회
			if(ifRowsNum>0){
				// IF 메타정보 생성
				tx_id = onSignalResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
				
				selectParamMap.put(info.getTxIdColName(), tx_id);
				selectParamMap.put(CJConstants.COL_EAI_FLAG, CJConstants.EAI_FLAG_STATUS_P);
				selectParamMap.put(CJConstants.ROW_NUM, info.getSendRowCount());
				
				int updatedRowsCnt = sqlSession.update(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SOURCE_UPDATE), selectParamMap);
				
				log.info("updatedRowsCnt : "  + updatedRowsCnt);
				
				pollDataList = sqlSession.selectList(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SELECT), selectParamMap);
				
				log.info("onSinalResult : "  + pollDataList.size() + " rows were selected. ");
			}
			
			onSignalResult.setPollResultDataObj(pollDataList);
			
			if (pollDataList != null) {
				onSignalResult.setResultCount(pollDataList.size());
				log.info("onSinalResult : "  + pollDataList.size() + " rows were selected. ");
			}
			
		}catch(Exception e){
			// 에러 발생 시 upate 후 commit했던 데이터들 다시 'N' 상태로 Update
			if(tx_id!=null && !"".equals(tx_id)){
				selectParamMap.put(info.getTxIdColName(), tx_id);
				selectParamMap.put(CJConstants.COL_EAI_FLAG, CJConstants.EAI_FLAG_STATUS_N);
				sqlSession.update(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SOURCE_UPDATE), selectParamMap);
			}
			log.info(e.getMessage());
			throw e;
		}
	}
}