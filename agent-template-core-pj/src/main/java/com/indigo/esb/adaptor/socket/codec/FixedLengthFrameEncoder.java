package com.indigo.esb.adaptor.socket.codec;

import java.nio.ByteOrder;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.indigo.esb.util.MTUtil;


@ChannelPipelineCoverage("all")

public class FixedLengthFrameEncoder extends OneToOneEncoder {
	private Log log = LogFactory.getLog(getClass());
	private final int frameLength;
	public FixedLengthFrameEncoder(int frameLength) {
			if (frameLength <= 0) {
	            throw new IllegalArgumentException(
	                    "frameLength must be a positive integer: " + frameLength);
	        }
	        this.frameLength = frameLength;
	}
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (!(msg instanceof byte[])) {
			log.error("msg not byte[]!!!!!");
			return msg;
		}
		log.debug("FixedLengthFrameEncoder");
		byte[] sBytes = (byte [])msg;
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer(ByteOrder.BIG_ENDIAN,this.frameLength);
		buf.writeBytes(sBytes,0,this.frameLength);
		return buf;
	}

}
