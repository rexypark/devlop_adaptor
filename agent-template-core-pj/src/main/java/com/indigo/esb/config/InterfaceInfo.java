package com.indigo.esb.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.indigo.esb.generator.ESBValueGenerator;

public class InterfaceInfo implements InitializingBean {

	protected String interfaceId;
	protected String targetDestinationName;
	protected String returnDestinationName;

	protected InterfaceType interfaceType;
	protected String targetMethod;
	protected boolean onSignalLoop = false;
	protected String sndSysId = "SND";        //송신 채널
	protected String rcvSysId = "RCV";        //수신 채널
 
	protected String onSignalPatternName;     //onSignal  메소드 
	protected String onMessagePatternName;    //onMessage 메소드 

	protected ESBValueGenerator txidGenerator;
	protected String txIdColName;
	protected String flagType;
	protected String patternType;

	protected Map<String, String> addDataMap = new HashMap<String, String>();

	protected String fileFilterPattern;    // 검색 파일 확장자 정의
	protected String tmpDir;               
	protected String scsDir;
	protected String errDir;
	protected String sendDir;
	protected String receiveDir;
	
	public ESBValueGenerator getTxidGenerator() {
		return txidGenerator;
	}
	
	public void setTxidGenerator(ESBValueGenerator txidGenerator) {
		this.txidGenerator = txidGenerator;
	}

	public String getTxIdColName() {
		return txIdColName;
	}

	public void setTxIdColName(String txIdColName) {
		this.txIdColName = txIdColName;
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

	public String getTargetMethod() {

		return targetMethod;
	}

	public void setTargetMethod(String targetMethod) {

		this.targetMethod = targetMethod;
	}

	public InterfaceInfo() {

	}

	public String getInterfaceId() {

		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {

		this.interfaceId = interfaceId;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		Assert.notNull(interfaceId, "Property 'interfaceId' is Required");
		// Assert.notNull(targetDestinationName,
		// "Property 'targetDestinationName' is Required");
	}

	public InterfaceType getInterfaceType() {

		return interfaceType;
	}

	public void setInterfaceType(InterfaceType interfaceType) {

		this.interfaceType = interfaceType;
	}

	public void setTargetDestinationName(String targetDestinationName) {

		this.targetDestinationName = targetDestinationName;
	}

	public String getTargetDestinationName() {

		return targetDestinationName;
	}

	@Override
	public String toString() {

		class ToStringStyleCustom extends ToStringStyle {

			private static final long serialVersionUID = 1L;

			public ToStringStyleCustom() {

				super.setNullText("[null]");
				this.setContentStart("[");
				this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
				this.setFieldSeparatorAtStart(true);
				this.setContentEnd(SystemUtils.LINE_SEPARATOR + "]");
			}
		}

		return ReflectionToStringBuilder.toString(this, new ToStringStyleCustom());
	}

	public String toLongString() {

		return toString();
	}

	public void setAddDataMap(Map<String, String> addDataMap) {

		this.addDataMap = addDataMap;
	}

	public Map<String, String> getAddDataMap() {

		return addDataMap;
	}

	public void setReturnDestinationName(String returnDestinationName) {

		this.returnDestinationName = returnDestinationName;
	}

	public String getReturnDestinationName() {

		return returnDestinationName;
	}

	public void setOnSignalPatternName(String onSignalPatternName) {

		this.onSignalPatternName = onSignalPatternName;
	}

	public String getOnSignalPatternName() {

		return onSignalPatternName;
	}

	public void setOnMessagePatternName(String onMessagePatternName) {

		this.onMessagePatternName = onMessagePatternName;
	}

	public String getOnMessagePatternName() {

		return onMessagePatternName;
	}

	public boolean isOnSignalLoop() {

		return onSignalLoop;
	}

	public void setOnSignalLoop(boolean onSignalLoop) {

		this.onSignalLoop = onSignalLoop;
	}

	public String getSndSysId() {

		return sndSysId;
	}

	public void setSndSysId(String sndSysId) {

		this.sndSysId = sndSysId;
	}

	public String getRcvSysId() {

		return rcvSysId;
	}

	public void setRcvSysId(String rcvSysId) {

		this.rcvSysId = rcvSysId;
	}

	public String getFlagType() {
		return flagType;
	}

	public void setFlagType(String flagType) {
		this.flagType = flagType;
	}

	public String getPatternType() {
		return patternType;
	}

	public void setPatternType(String patternType) {
		this.patternType = patternType;
	}

}
