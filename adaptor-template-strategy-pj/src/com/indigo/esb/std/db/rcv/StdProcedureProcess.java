package com.indigo.esb.std.db.rcv;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_SUCCESS;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;

/**
 * 프로시저 호출
 * 
 * @author clupine
 *
 */
public class StdProcedureProcess extends OnMessageSpacenameDBSupport {

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();

		if(ESB_TRANS_SUCCESS.equals(indigoMessageResult.getPocessStatus()) && dbInterfaceInfo.isStoredProcedure()){
			String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.CALL);
				int result =  sqlSession.update(getSqlId, indigoMessageResult.getProperties().getHeaderInfoMap());
				log.info("Procedure Call : " + result);
		}

	}
}
