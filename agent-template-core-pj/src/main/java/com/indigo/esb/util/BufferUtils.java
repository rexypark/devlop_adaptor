package com.indigo.esb.util;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class BufferUtils {

	public static String readString(ChannelBuffer buffer, int length,boolean nullAddBool) {
		byte[] dst;
		if(nullAddBool){
			 dst = new byte[length + 1];
		}else{
			dst = new byte[length];
		}
		buffer.readBytes(dst);
		String val = new String(dst).trim();
		return val;
	}
	
	public static ChannelBuffer makeErrorBuffer(String errCode, String errMsg) {
		ChannelBuffer cb = ChannelBuffers.buffer(146);
		cb.writerIndex(120);
		cb.writeBytes(errCode.getBytes());
		byte[] orgMsgBytes = errMsg.getBytes();
		if (orgMsgBytes.length > 20) {
			cb.writeBytes(orgMsgBytes, 0, 20);
		} else {
			cb.writeBytes(orgMsgBytes);
		}
		return cb;
	}

}
