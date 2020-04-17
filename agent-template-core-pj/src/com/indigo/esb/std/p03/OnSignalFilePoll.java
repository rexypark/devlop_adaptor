package com.indigo.esb.std.p03;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.file.OnSignalFileSupport;
import com.indigo.esb.config.FileInterfaceInfo;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

public class OnSignalFilePoll extends OnSignalFileSupport {

	private String serverInfo;
	public String fileExt = ".esb_scs";

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		List<Map<String, Object>> pollDataList = null;
		Map<String, String> onSignalValueMap = onSignalResult.getInterfaceInfo().getAddDataMap();
		FileInterfaceInfo info = (FileInterfaceInfo) onSignalResult.getInterfaceInfo();
		FileInterfaceInfo fileInterfaceInfo = (FileInterfaceInfo) onSignalResult.getInterfaceInfo();
		boolean remoteFlag = onSignalResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE).equals("REMOTE");
		String tx_id = onSignalResult.getProperty(ESB_TX_ID);
		String trgFile = null;
		String sendDir = fileInterfaceInfo.getSendDir();
		String tempDir = fileInterfaceInfo.getTmpDir();
		String scsDir = fileInterfaceInfo.getScsDir();
		String errDir = fileInterfaceInfo.getErrDir();
		if (checkedDir(sendDir) && checkedDir(tempDir) && checkedDir(scsDir) && checkedDir(errDir)) {

			FileFilter filter = null;
			String[] filePatternArray = null;
			File[] sendFiles = null;
			if (remoteFlag) {
				log.info("#### Remote Message Map : " + onSignalValueMap);
				log.info("Source File Name : " + onSignalValueMap.get("src_file"));
				if (onSignalValueMap.get("src_file") != null) {
					filePatternArray = onSignalValueMap.get("src_file").trim().split(",");
					filter = new WildcardFileFilter(filePatternArray, IOCase.SENSITIVE);
					onSignalResult.addProperty("src_file", onSignalValueMap.get("src_file"));
				}
				if (onSignalValueMap.get("src_path") != null) {
					sendDir = onSignalValueMap.get("src_path");
					onSignalResult.addProperty("src_path", sendDir);
				}

				if (onSignalValueMap.get("trg_path") != null)
					onSignalResult.addProperty("trg_path", onSignalValueMap.get("trg_path"));

				if (onSignalValueMap.get("trg_file") != null){
				    trgFile = onSignalValueMap.get("trg_file");
				    onSignalResult.addProperty("trg_file", onSignalValueMap.get("trg_file"));
				}
				if (onSignalValueMap.get("TIMEOUT") != null){
				    onSignalResult.addProperty("TIMEOUT", onSignalValueMap.get("TIMEOUT"));
				}
				if (onSignalValueMap.get("iftype") != null)
					onSignalResult.addProperty("iftype", onSignalValueMap.get("iftype"));
			} else {
				if (info.getFileFilterPattern() != null) {
					filePatternArray = info.getFileFilterPattern().trim().split(",");
					filter = new WildcardFileFilter(filePatternArray, IOCase.SENSITIVE);
				}
			}

			if (filter != null) {
				sendFiles = new File(sendDir).listFiles(filter);
			} else {
				sendFiles = new File(sendDir).listFiles();
			}

			StringBuffer fileNames = new StringBuffer();

			int cnt = 0;
			tempDir = tempDir + File.separator + onSignalResult.getProperty(ESB_TX_ID);
			if (sendFiles != null && sendFiles.length > 0) {
				for (File sendFile : sendFiles) {
					if (sendFile.getName().indexOf(getFileExt()) <= -1) {
						if (!new File(tempDir).exists())
							FileUtils.forceMkdir(new File(tempDir));
						if (new File(tempDir, sendFile.getName()).exists())
							new File(tempDir, sendFile.getName()).delete();
						if(!remoteFlag){
						    log.debug("move file");
						    FileUtils.moveFile(sendFile, new File(tempDir, sendFile.getName()));
						}else{
						    log.debug("copy file");
						    if(trgFile != null)
							FileUtils.copyFile(sendFile, new File(tempDir, trgFile));
						    else 
							FileUtils.copyFile(sendFile, new File(tempDir, sendFile.getName()));
						}
						fileNames.append(sendFile.getName());
						if (cnt < sendFiles.length - 1)
							fileNames.append(",");
						cnt++;
					}
				}
				onSignalResult.addProperty("org_file_name", fileNames.toString());
				log.debug("tempDir :" + tempDir + " trgFile : " + trgFile);
				if(!sendFile(tempDir, onSignalResult.getProperty(ESB_TX_ID), getServerInfo(), remoteFlag)){
				    throw new Exception("FILE SEND FAIL");
				}
				onSignalResult.setResultCount(cnt);
			}else{
			    log.error("SEND FILE NOT FOUND");
			    if(remoteFlag){
			    	throw new Exception("SEND FILE NOT FOUND");
			    }
			}

		} else {
			log.error("File Directory Make Fail !!");
			throw new Exception("File Directory Make Fail !!");
		}

		onSignalResult.setPollResultDataObj(pollDataList);
	}

	public boolean checkedDir(String dir) throws IOException {

		try {
			log.debug("dir : " + dir);
			if (!new File(dir).exists()) {
				FileUtils.forceMkdir(new File(dir));
				return true;
			} else {
				return true;
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	public String getServerInfo() {
		return serverInfo;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

}
