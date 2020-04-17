package com.indigo.esb.std.db.relational.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.config.InterfaceType;

public class PLCInterfaceInfo extends DBInterfaceInfo {
	
	protected String interfaceId;
	protected String targetDestinationName;
	protected String returnDestinationName;

	protected InterfaceType interfaceType = InterfaceType.DB;
	protected String targetMethod;
	protected boolean onSignalLoop = false;
	protected String sndSysId = "SND";
	protected String rcvSysId = "RCV";

	protected String onSignalPatternName;
	protected String onMessagePatternName;
	protected String subTableName;
	protected String srcFileNameColumns;
	protected String tmpDir;
	protected String attchFileTable;
	
	protected Map<String, String> addDataMap = new HashMap<String, String>();

	public String getAttchFileTable() {
		return attchFileTable;
	}

	public void setAttchFileTable(String attchFileTable) {
		this.attchFileTable = attchFileTable;
	}

	public String getSubTableName() {
		return subTableName;
	}

	public void setSubTableName(String subTableName) {
		this.subTableName = subTableName;
	}

	public String getTmpDir() {
		return tmpDir;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

	public String getSrcFileNameColumns() {
		return srcFileNameColumns;
	}

	public void setSrcFileNameColumns(String srcFileNameColumns) {
		this.srcFileNameColumns = srcFileNameColumns;
	}


	public String getTargetMethod() {

		return targetMethod;
	}

	public void setTargetMethod(String targetMethod) {

		this.targetMethod = targetMethod;
	}

	public PLCInterfaceInfo() {

      }

	public String getInterfaceId() {

		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {

		this.interfaceId = interfaceId;
	}

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

}
