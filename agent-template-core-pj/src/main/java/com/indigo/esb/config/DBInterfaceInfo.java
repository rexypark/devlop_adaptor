package com.indigo.esb.config;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.indigo.esb.generator.ESBValueGenerator;

public class DBInterfaceInfo extends InterfaceInfo {
	protected String targetDeleteTableName;
	protected String flagType;
	protected String patternType;
	protected String sourceTableName;
	protected String targetTableName;

	// Default 전송 Count를 1000으로 설정
	protected int sendRowCount = 1000;
	protected String resultColNames;
	protected String stateColName;
	protected String timeColName;
	protected String messageColName;
	protected ESBValueGenerator txidGenerator;
	protected String txIdColName;
	protected int commitCount;
	protected boolean storedProcedure;
	protected String ifUuidColName;
	int parallelCount = 4;
	int fixTotalCount;

	// 로그 에 실제 데이터 를 남길지 말지 판단(bmt)
	boolean noLogSendMsgbool = false;

	boolean filePathColumnExist = false;

	String filePathColumnName;

	public boolean isFilePathColumnExist() {
		return filePathColumnExist;
	}

	public String getFilePathColumnName() {
		return filePathColumnName;
	}

	public void setFilePathColumnName(String columnName) {
		filePathColumnExist = true;
		this.filePathColumnName = columnName;
	}

	public void setNoLogSendMsgbool(boolean noLogSendMsgbool) {
		this.noLogSendMsgbool = noLogSendMsgbool;
	}

	public boolean isNoLogSendMsgbool() {
		return noLogSendMsgbool;
	}

	public void setFixTotalCount(int fixTotalCount) {
		this.fixTotalCount = fixTotalCount;
	}

	public int getFixTotalCount() {
		return fixTotalCount;
	}

	public void setParallelCount(int parallelCount) {
		this.parallelCount = parallelCount;
	}

	public int getParallelCount() {
		return parallelCount;
	}

	String ip;
	int port;

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
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

	public String getTargetDeleteTableName() {
		return targetDeleteTableName;
	}

	public void setTargetDeleteTableName(String targetDeleteTableName) {
		this.targetDeleteTableName = targetDeleteTableName;
	}

	public String getIfUuidColName() {
		return ifUuidColName;
	}

	public void setIfUuidColName(String ifUuidColName) {
		this.ifUuidColName = ifUuidColName;
	}

	public void setMessageColName(String messageColName) {
		this.messageColName = messageColName;
	}

	public String getMessageColName() {
		return messageColName;
	}

	public boolean isStoredProcedure() {
		return storedProcedure;
	}

	public void setStoredProcedure(boolean storedProcedure) {
		this.storedProcedure = storedProcedure;
	}

	public String getResultColNames() {
		return resultColNames;
	}

	public void setResultColNames(String resultColNames) {
		this.resultColNames = resultColNames;
	}

	public String getTxIdColName() {
		return txIdColName;
	}

	public void setTxIdColName(String txIdColName) {
		this.txIdColName = txIdColName;
	}

	public String getStateColName() {
		return stateColName;
	}

	public void setStateColName(String stateColName) {
		this.stateColName = stateColName;
	}

	public String getTimeColName() {
		return timeColName;
	}

	public void setTimeColName(String timeColName) {
		this.timeColName = timeColName;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		setInterfaceType(InterfaceType.DB);
	}

	public void setCommitCount(int commitCount) {
		this.commitCount = commitCount;
	}

	public int getCommitCount() {
		return commitCount;
	}

	public String getSourceTableName() {
		return sourceTableName;
	}

	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
	}

	public String getTargetTableName() {
		return targetTableName;
	}

	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
	}

	public void setSendRowCount(int sendRowCount) {
		this.sendRowCount = sendRowCount;
	}

	public int getSendRowCount() {
		return sendRowCount;
	}

	public String getReturnColNames() {
		return resultColNames;
	}

	public void setReturnColNames(String resultColNames) {
		this.resultColNames = resultColNames;
	}

	@Override
	public void setOnSignalPatternName(String onSignalPatternName) {
		this.onSignalPatternName = onSignalPatternName;
	}

	@Override
	public void setOnMessagePatternName(String onMessagePatternName) {
		this.onMessagePatternName = onMessagePatternName;
	}

	public ESBValueGenerator getTxidGenerator() {
		return txidGenerator;
	}

	public void setTxidGenerator(ESBValueGenerator txidGenerator) {
		this.txidGenerator = txidGenerator;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
