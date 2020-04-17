package com.indigo.esb.std.file.snd;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_FILE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_FILES;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.OnSignalStrategy;
import com.indigo.esb.config.FileInterfaceInfo;
import com.indigo.esb.std.com.snd.EncryptionProcess;
import com.indigo.esb.util.DateUtil;
import com.indigo.fileserver.client.IndigoFileTransferAPI;

public class EachTXFileDataToJMS implements OnSignalStrategy {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	EncryptionProcess encryption = new EncryptionProcess();

	public void setEncryption(EncryptionProcess encryption) {
		this.encryption = encryption;
	}

	String serverInfo;
	
	int tryCnt = 1;

	public void setTryCnt(int tryCnt) {
		this.tryCnt = tryCnt;
	}

	public int getTryCnt() {
		return tryCnt;
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	public String getServerInfo() {
		return serverInfo;
	}

	@Autowired
	JmsTemplate jmsTemplate;

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		FileInterfaceInfo interfaceInfo = (FileInterfaceInfo) onSignalResult.getInterfaceInfo();
		String[] fileNames = onSignalResult.getProperty(ESB_FILES).split(",");
		onSignalResult.getProperties().getHeaderInfoMap().remove(ESB_FILES);

		for (String filename : fileNames) {
			logger.info("FileName : " + filename);
			File file = new File(interfaceInfo.getTmpDir(), filename);

			try {
				String sendDestinationName = onSignalResult.getInterfaceInfo().getTargetDestinationName();
				onSignalResult.addProperty(ESB_TX_ID, interfaceInfo.getTxidGenerator().create());
				onSignalResult.addProperty(ESB_SEND_ROW_COUNT, 1 + "");
				onSignalResult.addProperty(ESB_FILE, filename);
				onSignalResult.addProperty("ESB_FILESIZE", file.length() + "");
				encryption.onStart(onSignalResult);
				
				boolean sendSuccess = false;
				
				for (int i = 1; i <= tryCnt; i++) {
					if(socketSend(interfaceInfo.getTmpDir(), filename, serverInfo)){
						sendSuccess = true;
						break;
					}else{
						logger.info("Send Fail tryCount : {} " , i );
						Thread.sleep(3000L);
					}
				}
				
				if(!sendSuccess){
					throw new Exception("Send Fail , TryCountOver");
				}

				// 파일 송신후에
				this.jmsTemplate.convertAndSend(sendDestinationName, onSignalResult.getTemplateMessage(), onSignalResult.getProperties());
			} catch (Exception e) {
				logger.error(filename + " 파일 처리중 에러 발생 Error 디렉토리로 이동  ", e);
				FileUtils.moveFile(file, new File(interfaceInfo.getErrDir(), filename + "." + DateUtil.getTodayTimeMilli()));
				continue;
			}
			logger.info("송신 처리 완료 : " + file.getName());
			FileUtils.moveFile(file, new File(interfaceInfo.getScsDir(), filename + "." + DateUtil.getTodayTimeMilli()));
		}
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
			logger.info("File Socket Send Success !!!");
			return true;
		} catch (Exception e) {
			logger.info("File Socket Send Fail !!!");
			return false;
		}
	}

}
