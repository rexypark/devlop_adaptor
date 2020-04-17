package com.indigo.esb.std.file.snd;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_FILES;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.OnSignalStrategy;
import com.indigo.esb.config.FileInterfaceInfo;
import com.indigo.esb.config.FlatFileInterfaceInfo;

public class SimplePollingFileList implements OnSignalStrategy {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		Object obj = onSignalResult.getInterfaceInfo();
		String sendDir = null;
		String tempDir = null;
		String scsDir  = null;
		String errDir  = null;
		
		if(obj instanceof FlatFileInterfaceInfo){
			FlatFileInterfaceInfo info = (FlatFileInterfaceInfo)obj;
			
			 sendDir = info.getSendDir();
			 tempDir = info.getTmpDir();
			 scsDir = info.getScsDir();
			 errDir = info.getErrDir();
		}else if(obj instanceof FileInterfaceInfo){
			FileInterfaceInfo info = (FileInterfaceInfo)obj;
			
			 sendDir = info.getSendDir();
			 tempDir = info.getTmpDir();
			 scsDir = info.getScsDir();
			 errDir = info.getErrDir();
		}
		
		if (checkedDir(sendDir) && checkedDir(tempDir) && checkedDir(scsDir) && checkedDir(errDir)) {
			logger.info("SimplePollingFileList Start");
			StringBuffer fileNames = new StringBuffer();
			File[] sendFiles = new File(sendDir).listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					if(file.isFile()){
						return true;
					}
					return false;
				}
			});
			
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
				onSignalResult.addProperty(ESB_SEND_ROW_COUNT, "" + sendFiles.length);
				onSignalResult.addProperty(ESB_FILES, fileNames.toString());
				
			} else {
				onSignalResult.addProperty(ESB_SEND_ROW_COUNT, "0");
			}
			logger.info("SimplePollingFileList End");
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


}
