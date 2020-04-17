package com.indigo.esb.std.p03;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_SUCCESS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.file.OnMessageFileSupport;
import com.indigo.esb.config.FileInterfaceInfo;

public class MessageFileResultProcess extends OnMessageFileSupport {

	public boolean resultDel = false;
	public String fileExt = ".esb_scs";

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		String sendFileNameStr = indigoMessageResult.getProperty("org_file_name");
		String[] fileNames = sendFileNameStr.split(",");
		FileInterfaceInfo fileInfo = (FileInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		log.info("#### OnMessage Result process Message : " + indigoMessageResult.getProperties().getHeaderInfoMap());
		String sourcecDir = fileInfo.getSendDir();
		if (indigoMessageResult.getProperty("src_path") != null)
			sourcecDir = indigoMessageResult.getProperty("src_path");

		if (indigoMessageResult.getProperty(ESB_TRANS_STATUS).equals(ESB_TRANS_SUCCESS)) {
			if (isResultDel()) {
				resultDelProcess(fileNames, sourcecDir);
				log.info("Delete Processs Source Dir : " + sourcecDir);
			} else {
			    //resultMoveProcess(fileNames, fileInfo.getScsDir(), sourcecDir, getFileExt());
			    log.info("## File Trans Success srcPath : " + fileInfo.getScsDir() + " targetPath : " + sourcecDir);
			}
		} else {
		    resultMoveProcess(fileNames, fileInfo.getScsDir(), fileInfo.getErrDir());
		    log.info("## File Trans Fail srcPath : " + fileInfo.getScsDir() + " targetPath : " + fileInfo.getErrDir());
		}
	}

	public void resultDelProcess(String[] fileNames, String srcDir) throws IOException {
		for (String fileName : fileNames) {
			new File(srcDir, fileName).delete();
		}
	}

	public void resultMoveProcess(String[] fileNames, String srcDir, String trgDir) throws IOException {
		for (String fileName : fileNames) {
			if (new File(trgDir, fileName).exists()) {
				log.debug("Target File exist : " + trgDir + "/" + fileName);
				new File(trgDir, fileName).delete();
			}

			new File(srcDir, fileName).renameTo(new File(trgDir, fileName));
		}
	}

	public void resultMoveProcess(String[] fileNames, String srcDir, String trgDir, String ext) throws IOException {
		for (String fileName : fileNames) {
			if (new File(trgDir, fileName + ext).exists())
				new File(trgDir, fileName + ext).delete();

			new File(srcDir, fileName).renameTo(new File(trgDir, fileName + ext));
		}
	}

	public void setResultDel(boolean resultDel) {
		this.resultDel = resultDel;
	}

	public boolean isResultDel() {
		return resultDel;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public String getFileExt() {
		return fileExt;
	}
}
