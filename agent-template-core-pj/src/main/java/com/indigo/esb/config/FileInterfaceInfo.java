package com.indigo.esb.config;

import java.util.Map;
import java.util.Set;


public class FileInterfaceInfo extends InterfaceInfo {

	protected String fileFilterPattern;
	protected String tmpDir;
	protected String scsDir;
	protected String errDir;
	protected String copyDir;
	protected String sendDir;
	protected String sendDir2;
	protected String receiveDir;
	protected String resendDir;
	protected Set<String> fields;
	protected Map<String,String> changeFields;
	protected String icdFilePath;
	
	public void setChangeFields(Map<String, String> changeFields) {
		this.changeFields = changeFields;
	}
	public void setIcdFilePath(String icdFilePath) {
		this.icdFilePath = icdFilePath;
	}
	public String getIcdFilePath() {
		return icdFilePath;
	}
	public Map<String, String> getChangeFields() {
		return changeFields;
	}
	public String getSendDir2() {
		return sendDir2;
	}

	public void setSendDir2(String sendDir2) {
		this.sendDir2 = sendDir2;
	}

	public void setFields(Set<String> fields) {
		this.fields = fields;
	}
	
	public void setCopyDir(String copyDir) {
		this.copyDir = copyDir;
	}
	public String getCopyDir() {
		return copyDir;
	}
	
	public Set<String> getFields() {
		return fields;
	}
	
	public String getFileFilterPattern() {
		return fileFilterPattern;
	}

	public void setFileFilterPattern(String fileFilterPattern) {
		this.fileFilterPattern = fileFilterPattern;
	}

	public String getTmpDir() {
		return tmpDir;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

	public String getScsDir() {
		return scsDir;
	}

	public void setScsDir(String scsDir) {
		this.scsDir = scsDir;
	}

	public String getErrDir() {
		return errDir;
	}

	public void setErrDir(String errDir) {
		this.errDir = errDir;
	}

	public String getSendDir() {
		return sendDir;
	}

	public void setSendDir(String sendDir) {
		this.sendDir = sendDir;
	}

	public String getReceiveDir() {
		return receiveDir;
	}

	public void setReceiveDir(String receiveDir) {
		this.receiveDir = receiveDir;
	}
	
	
	public String getResendDir() {
		return resendDir;
	}

	public void setResendDir(String resendDir) {
		this.resendDir = resendDir;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		setInterfaceType(InterfaceType.FILE);
	}

}
