package com.indigo.esb.adaptor.file;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.file.OnMessageFileSupport;
import com.indigo.esb.config.FileInterfaceInfo;
import com.indigo.esb.util.ZipUtils;

public class DefaultOnMessageFileProcessImpl extends OnMessageFileSupport {

	public String serverInfo;

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		FileInterfaceInfo fileInterfaceInfo = (FileInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		String receiveDir = fileInterfaceInfo.getReceiveDir();
		String tempDir = fileInterfaceInfo.getTmpDir();
		String errDir = fileInterfaceInfo.getErrDir();

		if (checkedDir(receiveDir) && checkedDir(tempDir) && checkedDir(errDir)) {
		}

		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(ESB_TX_ID);
		if (socketReceive(tx_id + ".zip", tempDir, getServerInfo())) {
			ZipUtils.decompress(new File(receiveDir), new File(tempDir, tx_id + ".zip"));
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
