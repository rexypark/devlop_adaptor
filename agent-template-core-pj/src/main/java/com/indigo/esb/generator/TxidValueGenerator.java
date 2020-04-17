package com.indigo.esb.generator;

import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.time.DateFormatUtils;

public class TxidValueGenerator implements ESBValueGenerator {

	String if_id = "ESB0000";

	public void setIf_id(String if_id) {
		this.if_id = if_id;
	}

	@Override
	public String create() {
		String initTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
		Random random = new Random(System.currentTimeMillis());
		int randomNumber = -1;
		while ((randomNumber = random.nextInt(100000)) < 10000)
			;
		return if_id + "_" + initTime + "_" + String.valueOf(randomNumber);
	}

}
