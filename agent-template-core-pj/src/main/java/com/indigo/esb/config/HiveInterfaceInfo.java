package com.indigo.esb.config;

import java.util.Set;

import com.indigo.esb.generator.ESBValueGenerator;

/**
 * @author clupine 일단 수신 인터페이스 만
 */
public class HiveInterfaceInfo extends InterfaceInfo {
	protected String patternType;
	protected String targetTableName;
	protected ESBValueGenerator txidGenerator;
	protected String hdfsUrl;
	protected String hdfsSaveDir;
	protected String fsName = "fs.default.name";
	protected Set<String> keySet;
	
	
	protected String resultColNames;
	protected String stateColName;
	protected String timeColName;
	
	

	public String getResultColNames() {
		return resultColNames;
	}
	public void setResultColNames(String resultColNames) {
		this.resultColNames = resultColNames;
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
	public void setFsName(String fsName) {
		this.fsName = fsName;
	}
	public String getFsName() {
		return fsName;
	}
	
	public Set<String> getKeySet() {
		return keySet;
	}

	public void setKeySet(Set<String> keySet) {
		this.keySet = keySet;
	}

	public String getHdfsUrl() {
		return hdfsUrl;
	}

	public void setHdfsUrl(String hdfsUrl) {
		this.hdfsUrl = hdfsUrl;
	}

	public String getHdfsSaveDir() {
		return hdfsSaveDir;
	}

	public void setHdfsSaveDir(String hdfsSaveDir) {
		this.hdfsSaveDir = hdfsSaveDir;
	}

	public String getPatternType() {
		return patternType;
	}

	public void setPatternType(String patternType) {
		this.patternType = patternType;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		setInterfaceType(InterfaceType.HIVE);
	}

	public String getTargetTableName() {
		return targetTableName;
	}

	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
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

}
