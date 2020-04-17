package com.indigo.esb.util;

public enum MCIMTMappingTypeEnum {
	A("A"), N("N"), KHH("KHH"), KFH("KFH"), EH("EH"), EHNOSOSI("EHNOSOSI"), V("V");

	private String attrStr;

	MCIMTMappingTypeEnum(String attrStr) {
		this.attrStr = attrStr.toUpperCase();
	}

	String getAttrString() {
		return attrStr;
	}

}
