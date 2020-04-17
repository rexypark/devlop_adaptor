package com.indigo.esb.std.generator;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.indigo.esb.generator.ESBValueGenerator;

public class CJTxidValueGenerator implements ESBValueGenerator {

	String systemCode = "ESB";
	int seq = 0;

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	@Override
	public String create() {
		if(seq>999){
			seq = 0;
		}
		String initTime = DateFormatUtils.format(new Date(), "yyMMddHHmmssSSS");

		return systemCode + initTime + String.format("%03d", seq++);
	}
}