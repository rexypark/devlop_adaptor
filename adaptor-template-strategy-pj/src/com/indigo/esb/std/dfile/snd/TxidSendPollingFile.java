package com.indigo.esb.std.dfile.snd;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_FILE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.io.File;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.std.dfile.handler.DB2FileWriteHandler;

/**
 * Table Select
 * 
 * @author clupine
 *
 */
public class TxidSendPollingFile extends OnSignalSpacenameDBSupport {

	private String filePath;

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		@SuppressWarnings("rawtypes")
		Map headerMap = onSignalResult.getProperties().getHeaderInfoMap();

		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();
		String txId = onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		File file = new File(getFilePath()+File.separator+txId);
		DB2FileWriteHandler handler = new DB2FileWriteHandler(file , sqlSession , headerMap , getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SOURCE_UPDATE));
		headerMap.put(info.getTxIdColName(), txId);
		sqlSession.select(getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SELECT ),headerMap, handler);
		log.info("count : " + handler.getCount());

		if(handler.getCount() > 0){
			handler.update();
			handler.close();
			onSignalResult.addProperty(ESB_SEND_ROW_COUNT, 1 + "");
			onSignalResult.addProperty(ESB_FILE, file.getCanonicalPath());			
			onSignalResult.addProperty("ESB_FILESIZE", file.length() + "");
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}	

}
