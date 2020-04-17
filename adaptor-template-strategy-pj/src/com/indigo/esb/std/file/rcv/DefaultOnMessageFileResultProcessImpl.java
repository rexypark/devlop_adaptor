package com.indigo.esb.std.file.rcv;

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

		String sendFileNameStr = indigoMessageResult.getProperty("file_name");
		String[] fileNames = sendFileNameStr.split(",");
		FileInterfaceInfo fileInfo = (FileInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		String tx_id = indigoMessageResult.getProperty("tx_id");
		String resendDir = fileInfo.getResendDir()+ File.separator + tx_id;
		
		if (indigoMessageResult.getProperty(ESB_TRANS_STATUS).equals(ESB_TRANS_SUCCESS)) {
			for (String fileName : fileNames) {
				//new File(fileInfo.getScsDir(), fileName).delete(); //for snd_org check success file list
				new File(resendDir, fileName).delete();
			}
			File resend = new File(resendDir);
			if (resend.exists()){
				resend.delete();
			}
		} else {
			for (String fileName : fileNames) {
				if (new File(fileInfo.getErrDir(), fileName).exists())
					new File(fileInfo.getErrDir(), fileName).delete();
				FileUtils.forceMkdir(new File(resendDir));
				FileUtils.copyFile(new File(fileInfo.getScsDir(), fileName), new File(resendDir, fileName));
				FileUtils.moveFile(new File(fileInfo.getScsDir(), fileName), new File(fileInfo.getErrDir(), fileName));
			}
		}

	}

}
