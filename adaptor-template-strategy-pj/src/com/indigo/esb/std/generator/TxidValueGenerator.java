package com.indigo.esb.std.generator;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.indigo.esb.generator.ESBValueGenerator;

public class TxidValueGenerator implements ESBValueGenerator {

	String alias = "ESB";
	int seq = 0;

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Override
	public String create() {
		if(seq>999){
			seq = 0;
		}
		String initTime = DateFormatUtils.format(new Date(), "yyMMddHHmmssSSS");

		return alias + initTime + String.format("%03d", seq++);
	}
}