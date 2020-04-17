package com.indigo.esb.adaptor.file;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_SUCCESS;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.file.OnMessageFileSupport;
import com.indigo.esb.config.FileInterfaceInfo;

public class DefaultOnMessageFileResultProcessImpl extends OnMessageFileSupport {

	@SuppressWarnings("unchecked")
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		String sendFileNameStr = indigoMessageResult.getProperty("org_file_name");
		String[] fileNames = sendFileNameStr.split(",");
		FileInterfaceInfo fileInfo = (FileInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		if (indigoMessageResult.getProperty(ESB_TRANS_STATUS).equals(ESB_TRANS_SUCCESS)) {
			for (String fileName : fileNames) {
				new File(fileInfo.getScsDir(), fileName).delete();
			}
		} else {
			for (String fileName : fileNames) {
				if (new File(fileInfo.getErrDir(), fileName).exists())
					new File(fileInfo.getErrDir(), fileName).delete();
				FileUtils.moveFile(new File(fileInfo.getScsDir(), fileName), new File(fileInfo.getErrDir(), fileName));
			}
		}

	}

}
