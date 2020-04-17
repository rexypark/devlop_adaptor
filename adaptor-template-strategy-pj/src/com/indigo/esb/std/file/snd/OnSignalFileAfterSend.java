package com.indigo.esb.std.file.snd;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.file.OnSignalFileSupport;
import com.indigo.esb.config.FileInterfaceInfo;

/**
 * tempDir ������ �����Ѵ�
 * 
 * @author Administrator
 *
 */
public class OnSignalFileAfterSend extends OnSignalFileSupport {

      
	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		String fileNameStr = onSignalResult.getProperty("file_name");
		Map<String, String> onSignalValueMap = onSignalResult.getInterfaceInfo().getAddDataMap();
		if (fileNameStr != null) {
			String[] fileNames = fileNameStr.split(",");
			FileInterfaceInfo fileinterfaceInfo = (FileInterfaceInfo) onSignalResult.getInterfaceInfo();
			for (String sendFile : fileNames) {
				if (new File(fileinterfaceInfo.getScsDir(), sendFile).exists())
					new File(fileinterfaceInfo.getScsDir(), sendFile).delete();
				if(!onSignalValueMap.containsKey(ESB_IF_DATA_TYPE)){
				    FileUtils.moveFile(
					    new File(fileinterfaceInfo.getTmpDir() + File.separator + onSignalResult.getProperty("tx_id"),
						    sendFile), new File(fileinterfaceInfo.getScsDir(), sendFile));
				}
			}
			FileUtils.deleteDirectory(new File(fileinterfaceInfo.getTmpDir() + File.separator + onSignalResult.getProperty("tx_id") + File.separator));
		}

	}
	
}
