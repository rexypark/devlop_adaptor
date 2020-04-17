package com.indigo.esb.adaptor.strategy.file;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.strategy.OnSignalStrategy;
import com.indigo.esb.util.ZipUtils;
import com.indigo.fileserver.client.IndigoFileTransferAPI;

/**
 *
 * @author yoonjonghoon
 *
 */
public abstract class OnSignalFileSupport implements OnSignalStrategy, InitializingBean {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired(required = false)
	protected JmsTemplate jmsTemplate;

	public boolean sendFile(String sendDir, String tx_id, String serverInfo, boolean delFlag) throws Exception {

		File tempDir = new File(sendDir);

		File[] zipFileList = tempDir.listFiles();
		log.debug("tmpDirList :" +  tempDir);
		ZipUtils.compress(zipFileList, tempDir.getAbsolutePath(), new File(sendDir + File.separator + tx_id + ".zip"));
		log.info("=========# make zip file end =======");
		String[] svrInfo = serverInfo.split("\\,");
		for (int i = 0; i < svrInfo.length; i++) {
		    if(socketSend(sendDir,  tx_id + ".zip", svrInfo[i])){
			    log.info("=========# make excel file send end =======");
			    if(delFlag){
			          new File(sendDir, tx_id + ".zip").delete();
			    }
			    return true;
		    }
		}
		return false;
	}

	public boolean socketSend(String filePath,  String fileName, String serverInfo) {
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

	@Override
	public void afterPropertiesSet() throws Exception {

	}

}
