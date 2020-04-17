package com.indigo.esb.adaptor.file;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.file.OnSignalFileSupport;
import com.indigo.esb.config.FileInterfaceInfo;

public class DefaultOnSignalFileAfterSendImpl extends OnSignalFileSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		String fileNameStr = onSignalResult.getProperty("org_file_name");
		if (fileNameStr != null) {
			String[] fileNames = fileNameStr.split(",");
			FileInterfaceInfo fileinterfaceInfo = (FileInterfaceInfo) onSignalResult.getInterfaceInfo();
			for (String sendFile : fileNames) {
				if (new File(fileinterfaceInfo.getScsDir(), sendFile).exists())
					new File(fileinterfaceInfo.getScsDir(), sendFile).delete();
				FileUtils.moveFile(new File(fileinterfaceInfo.getTmpDir(), sendFile),
						new File(fileinterfaceInfo.getScsDir(), sendFile));
			}
		}

	}

}
