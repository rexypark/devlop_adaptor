package com.indigo.esb.std.hadoop.rcv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.HiveInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * HDFS 파일 Hive 테이블에 Insert
 * 
 * @author clupine
 *
 */
public class HiveReceiveProcess extends OnMessageSpacenameDBSupport {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void process(IndigoMessageResult indigoMessageResult)
			throws Exception {

		HiveInterfaceInfo ifInfo = (HiveInterfaceInfo) indigoMessageResult
				.getInterfaceInfo();
		
		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		String importFilePath = ifInfo.getHdfsSaveDir()+"/"+tx_id;
		
		//file
		String getSqlId = getMybaitsSqlId(ifInfo.getInterfaceId(), QueryStatementType.INSERT);
		
		int insertCnt = sqlSession.update(getSqlId, importFilePath);

		log.info("Process Hive LOAD DATA Result : " + insertCnt);
		
	}
}
