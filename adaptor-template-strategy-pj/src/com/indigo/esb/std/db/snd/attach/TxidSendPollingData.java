package com.indigo.esb.std.db.snd.attach;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.fileserver.client.IndigoFileTransferAPI;

/**
 * 첨부파일 송신
 * 
 * @author clupine
 *
 */
public class TxidSendPollingData extends com.indigo.esb.std.db.snd.TxidSendPollingData {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		super.onStart(onSignalResult);

		List<Map<String, Object>> pollDataList = (List<Map<String, Object>>) onSignalResult.getPollResultDataObj();

		Map headerMap = onSignalResult.getProperties().getHeaderInfoMap();
		String txid = (String) headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);

		DBInterfaceInfo info = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();
		if (pollDataList == null || pollDataList.size() == 0) {
			return;
		}

		// 첨부파일 찾아서 송신하기
		// <entry key="ATTACH_COLUMN" value="PIC_NAME" />
		// <entry key="ATTACH_PATH" value="D:/send" />

		String attachColumn = info.getAddDataMap().get("ATTACH_COLUMN");
		String attachPath = info.getAddDataMap().get("ATTACH_PATH");
		String attachPathMove = info.getAddDataMap().get("ATTACH_PATH_MOVE");
		String serverInfo = info.getAddDataMap().get("SERVER_INFO");
		int tryCnt = info.getAddDataMap().get("TRY_CNT") == null ? 3
				: Integer.valueOf(info.getAddDataMap().get("TRY_CNT"));

		File[] preMoveTargetFiles = new File[pollDataList.size()];

		int cnt = 0;

		for (Map<String, Object> map : pollDataList) {

			String fileName = (String) map.get(attachColumn);
			String sendFileStr = attachPath + File.separator + fileName;
			log.info("send File : " + sendFileStr);

			preMoveTargetFiles[cnt++] = new File(sendFileStr);

			for (int i = 1; i <= tryCnt; i++) {

				boolean result = socketSend(txid, sendFileStr, serverInfo);
				if (result) {
					log.info("try Cnt " + i + "put success : " + sendFileStr);
					break;
				} else {
					log.info("try Cnt " + i + "put Fail : " + sendFileStr);
					if (tryCnt == i) {
						throw new RuntimeException("File put Fail ==> " + sendFileStr);
					}
				}
			}
		}

		if (attachPathMove != null) {
			File moveDir = new File(attachPathMove + txid);
			FileUtils.forceMkdir(moveDir);
			moveFileToDirectory(preMoveTargetFiles, moveDir, true);
		}

	}

	private void moveFileToDirectory(File[] files, File destDir, boolean createDestDir) throws IOException {

		for (File file : files) {
			File existFile = new File(destDir.getCanonicalPath() + File.separator + file.getName());
			if (existFile.exists()) {
				FileUtils.forceDelete(existFile);
			}
			FileUtils.moveFileToDirectory(file, destDir, createDestDir);
			log.info("moved " + file.getCanonicalPath() + " ==> " + destDir.getCanonicalPath());
		}
	}

	public boolean socketSend(String txid, String file, String serverInfo) {
		try {

			String serverTemp[] = serverInfo.split(":");
			String serverIp = serverTemp[0];
			int serverPort = Integer.parseInt(serverTemp[1]);
			IndigoFileTransferAPI api = new IndigoFileTransferAPI(serverIp, serverPort);
			api.setChunkSize(8192);
			api.simpleFilePut(txid, file);
			log.info("File Socket Send Success !!!");
			return true;
		} catch (Exception e) {
			log.error("File Socket Send Fail !!! : ", e);
			return false;
		}
	}

}
