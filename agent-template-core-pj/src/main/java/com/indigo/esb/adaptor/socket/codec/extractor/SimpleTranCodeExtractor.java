package com.indigo.esb.adaptor.socket.codec.extractor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;

public class SimpleTranCodeExtractor implements TranCodeExtractor {
	
	private final Log log = LogFactory.getLog(getClass());
	
	int startIdx;
	
	int length;

	public SimpleTranCodeExtractor() {
		
	}

	public String extract(ChannelBuffer buffer) {
		
		log.debug("## TranCode 추출할 Buffer : " + buffer.toString());
		log.debug("## TranCode startIdx : " + startIdx);
		log.debug("## TranCode length : " + length);
		
//		byte[] bb = new byte[buffer.readableBytes()];
//		
//		buffer.readBytes(bb);
//		
		byte [] dst = new byte[length];
		
		buffer.getBytes(startIdx, dst);
		log.debug("## 추출할 dst : "+new String(dst));
		return new String(dst);
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
