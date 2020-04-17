package com.indigo.esb.config;

import java.util.Set;


public class FlatFileInterfaceInfo extends InterfaceInfo {
	
	Set<String> nameSet;
	boolean firstRowSkip;
	
	protected String fileFilterPattern;
	protected String tmpDir;
	protected String scsDir;
	protected String errDir;
	protected String sendDir;
	
	String FileType = "AUTO";
	
	public void setFileType(String fileType) {
		FileType = fileType;
	}
	
	public String getFileType() {
		return FileType;
	}
	
	public void setFirstRowSkip(boolean firstRowSkip) {
		this.firstRowSkip = firstRowSkip;
	}
	
	public boolean isFirstRowSkip() {
		return firstRowSkip;
	}
	
	public void setNameSet(Set<String> nameSet) {
		this.nameSet = nameSet;
	}
	
	public Set<String> getNameSet() {
		return nameSet;
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

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		setInterfaceType(InterfaceType.FILE);
	}

	@Override
	public String toString() {
		return "FlatFileInterfaceInfo [nameSet=" + nameSet + ", firstRowSkip=" + firstRowSkip + ", fileFilterPattern=" + fileFilterPattern
				+ ", tmpDir=" + tmpDir + ", scsDir=" + scsDir + ", errDir=" + errDir + ", sendDir=" + sendDir + ", FileType=" + FileType
				+ "]";
	}

}
