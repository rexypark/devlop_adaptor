package com.indigo.esb.std.db.rcv.attach;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.std.db.rcv.QueryMapper;
import com.indigo.fileserver.client.IndigoFileTransferAPI;

/**
 * 첨부 파일 수신 처리
 * 
 * @author clupine
 *
 */
public class TxidReceiveProcess extends com.indigo.esb.std.db.rcv.TxidReceiveProcess {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		super.process(indigoMessageResult);

		DBInterfaceInfo info = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		Map headerMap = indigoMessageResult.getProperties().getHeaderInfoMap();
		String txid = (String) headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> rowMapList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		Map<String, String> addDataMap = info.getAddDataMap();

		// 첨부파일 찾아서 송신하기
		// <entry key="ATTACH_COLUMN" value="PIC_NAME" />
		// <entry key="ATTACH_PATH" value="D:/recv" />

		String attachColumn = addDataMap.get("ATTACH_COLUMN");
		String attachPath = addDataMap.get("ATTACH_PATH");
		String attachTemp = addDataMap.get("ATTACH_TEMP");
		String serverInfo = addDataMap.get("SERVER_INFO");
		int tryCnt = info.getAddDataMap().get("TRY_CNT") == null ? 3
				: Integer.valueOf(info.getAddDataMap().get("TRY_CNT"));
		String attachTxidTempPath = attachTemp+File.separator+txid;
		

		for (Map<String, Object> map : rowMapList) {

			String fileName = (String) map.get(attachColumn);
			String recvFileStr = attachTxidTempPath + File.separator + fileName;

			log.info("recv File : " + recvFileStr);

			for (int i = 1; i <= tryCnt; i++) {
				boolean result = socketRecv(txid, attachTemp, fileName, serverInfo);
				if (result) {
					log.info("try Cnt " + i + " get success : " + recvFileStr);
					break;
				} else {
					log.info("try Cnt " + i + " get Fail : " + recvFileStr);
					if (tryCnt == i) {
						throw new RuntimeException("File get Fail ==> " + recvFileStr);
					}
				}
			}
		}
		
		
		File attachTxidTempDir = new File(attachTxidTempPath);
		
		moveFileToDirectory(attachTxidTempDir.listFiles(), new File(attachPath), true);
		
		FileUtils.forceDelete(attachTxidTempDir);
		
	}
	
	private void moveFileToDirectory(File[] files, File destDir , boolean createDestDir ) throws IOException {
		
		
		for (File file : files) {
			File existFile = new File(destDir.getCanonicalPath()+File.separator + file.getName());
			if(existFile.exists()) {
				FileUtils.forceDelete(existFile);
			}
			FileUtils.moveFileToDirectory(file, destDir, createDestDir);
			logger.info("moved " + file.getCanonicalPath() + " ==> " + destDir.getCanonicalPath());
		}
	}

	public boolean socketRecv(String txid, String storePath, String fileName, String serverInfo) {
		try {
			String serverTemp[] = serverInfo.split(":");
			String serverIp = serverTemp[0];
			int serverPort = Integer.parseInt(serverTemp[1]);
			IndigoFileTransferAPI api = new IndigoFileTransferAPI(serverIp, serverPort);
			api.setRepository(storePath);
			api.setChunkSize(8192);
			api.simpleFileGet(txid, fileName);
			log.info("File Socket Recv Success !!!");
			return true;
		} catch (Exception e) {
			log.error("File Socket Recv Fail !!! : ", e);
			return false;
		}
	}

	/**
	 * 테이블 삭제 처리
	 * 
	 * @param session
	 * @param tableName
	 * @throws Exception
	 */
	public void deleteTable(SqlSession session, String tableName) throws Exception {
		try {
			if (!session.getConfiguration().getMapperRegistry().hasMapper(QueryMapper.class)) {
				session.getConfiguration().addMapper(QueryMapper.class);
			}

			QueryMapper mapper = session.getMapper(QueryMapper.class);
			HashMap<String, String> queryMap = new HashMap<String, String>();
			queryMap.put("query", "DELETE " + tableName);
			mapper.delete(queryMap);

		} catch (Exception e) {
			logger.error("Exception While Delete Table : " + e.getMessage());
			throw e;
		}
	}
}
