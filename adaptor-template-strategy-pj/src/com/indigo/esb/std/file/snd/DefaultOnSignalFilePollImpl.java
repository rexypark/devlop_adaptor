package com.indigo.esb.std.file.snd;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.file.OnSignalFileSupport;
import com.indigo.esb.config.FileInterfaceInfo;

public class DefaultOnSignalFilePollImpl extends OnSignalFileSupport {

	public String serverInfo;

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		FileInterfaceInfo fileInterfaceInfo = (FileInterfaceInfo) onSignalResult.getInterfaceInfo();
		String sendDir = fileInterfaceInfo.getSendDir();
		String tempDir = fileInterfaceInfo.getTmpDir();
		String scsDir = fileInterfaceInfo.getScsDir();
		String errDir = fileInterfaceInfo.getErrDir();

		if (checkedDir(sendDir) && checkedDir(tempDir) && checkedDir(scsDir) && checkedDir(errDir)) {

			File[] sendFiles = new File(sendDir).listFiles();
			StringBuffer fileNames = new StringBuffer();
			if (sendFiles != null && sendFiles.length > 0) {
				int cnt = 0;
				for (File sendFile : sendFiles) {
					if (new File(tempDir, sendFile.getName()).exists())
						new File(tempDir, sendFile.getName()).delete();
					FileUtils.moveFile(sendFile, new File(tempDir, sendFile.getName()));
					fileNames.append(sendFile.getName());
					if (cnt < sendFiles.length - 1)
						fileNames.append(",");
					cnt++;
				}
				onSignalResult.addProperty("org_file_name", fileNames.toString());
				sendFile(tempDir, onSignalResult.getProperty(ESB_TX_ID), getServerInfo(), true);
				onSignalResult.addProperty(ESB_SEND_ROW_COUNT, "" + sendFiles.length);
			} else {
				onSignalResult.addProperty(ESB_SEND_ROW_COUNT, "0");
			}
		}

	}

	public boolean checkedDir(String dir) throws IOException {
		if (!new File(dir).exists()) {
			FileUtils.forceMkdir(new File(dir));
			return true;
		} else {
			return true;
		}
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	public String getServerInfo() {
		return serverInfo;
	}

}
