package com.indigo.esb.std.dfile.snd;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.log.ESBLogging;
import com.indigo.fileserver.client.IndigoFileTransferAPI;

/**
 * Table Select
 * 
 * @author clupine
 *
 */
public class TxidFileSocketSend extends OnSignalSpacenameDBSupport {

	private String serverInfo;
	// private String filePath;

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();

		String fileName = onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_FILE);
		String filePath = fileName.substring(0, fileName.lastIndexOf(File.separator));

		log.info("FileName : " + fileName);

		String txid = onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);

		try {
			if (socketSend(filePath, txid, getServerInfo())) {
				log.info("file send success!");
				fileDelete(fileName);
			} else {
				log.info("file send fail!");

				onSignalResult.addProperty(ESB_SEND_ROW_COUNT, 0 + "");

				DBUpdate(info, txid, "F");
				fileDelete(fileName);
				ESBLogging.oneLogging(jmsTemplate, info.getInterfaceId(),
						onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_TX_ID),
						Integer.parseInt(
								onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT)),
						1, info.getSndSysId(), info.getRcvSysId(),
						onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME),
						ESBLogging.parseDateString(new Date(), "yyyyMMddHHmmssSSS"),
						ESBLogging.parseDateString(new Date(), "yyyyMMddHHmmssSSS"), "Socket File Send Fail", "F");

			}
		} catch (Exception e) {
			log.error("file send error", e);
			DBUpdate(info, txid, "F");
			fileDelete(fileName);
			ESBLogging.oneLogging(jmsTemplate, info.getInterfaceId(),
					onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_TX_ID),
					Integer.parseInt(onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT)),
					1, info.getSndSysId(), info.getRcvSysId(),
					onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME),
					ESBLogging.parseDateString(new Date(), "yyyyMMddHHmmssSSS"),
					ESBLogging.parseDateString(new Date(), "yyyyMMddHHmmssSSS"), e.getMessage(), "F");
		}

	}

	private void DBUpdate(DBInterfaceInfo info, String txid, String status) {
		int updateCnt = 0;

		Map<String, String> paramMap = new HashMap<String, String>();

		String statusColName = info.getStateColName();
		String txidColName = info.getTxIdColName();

		if (statusColName == null) {
			statusColName = IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS;
		}
		if (txidColName == null) {
			txidColName = IndigoHeaderJMSPropertyConstants.ESB_TX_ID;
		}

		paramMap.put(statusColName, status);
		paramMap.put(txidColName, txid);

		String getSqlId = getMybaitsSqlId(info.getInterfaceId(), QueryStatementType.SOURCE_RESULT_UPDATE);
		updateCnt += sqlSession.update(getSqlId, paramMap);

		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			log.info("Result Process Batch Update Count : "
					+ sqlSession.flushStatements().get(0).getUpdateCounts().length);
		} else {
			log.info("Result Process Update Count : " + updateCnt);
		}

	}

	public void fileDelete(String fileName) {
		new File(fileName).delete();
		log.debug("File delete Ok.");
	}

	public boolean socketSend(String filePath, String fileName, String serverInfo) {
		try {
			String serverTemp[] = serverInfo.split(":");
			String serverIp = serverTemp[0];
			int serverPort = Integer.parseInt(serverTemp[1]);

			IndigoFileTransferAPI api = new IndigoFileTransferAPI(serverIp, serverPort);
			api.setChunkSize(8192);
			String file = filePath + File.separator + fileName;
			api.simpleFilePut("", file);
			log.info("File Socket Send Success !!!");
			return true;
		} catch (Exception e) {
			log.info("File Socket Send Fail !!!");
			return false;
		}
	}

	public String getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

}
