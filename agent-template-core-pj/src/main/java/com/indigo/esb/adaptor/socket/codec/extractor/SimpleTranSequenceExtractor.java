package com.indigo.esb.adaptor.socket.codec.extractor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;

public class SimpleTranSequenceExtractor implements TranSequenceExtractor {

	private final Log log = LogFactory.getLog(getClass());
	
	int startIdx;

	int length;

	public SimpleTranSequenceExtractor() {
	}

	public String extract(String tranCode, ChannelBuffer buffer) {
		try{
			byte[] dst = new byte[length];
			buffer.getBytes(startIdx, dst);
			return new String(dst);
		}catch (Exception e) {
			log.info("#### 시퀀스 추출 실패 공백으로 리턴");
		}
		
		return "";
	}

	public int getStartIdx() {
		return startIdx;
	}

	public void setStartIdx(int startIdx) {
		this.startIdx = startIdx;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
}
