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

public class StringLengthFieldBasedFrameEncoder extends OneToOneEncoder {
	private Log log = LogFactory.getLog(getClass());
	private int maxFrameLength;
	private int lengthFieldOffset;
	private int lengthFieldLength;
	private int lengthFieldEndOffset;
	private int lengthAdjustment;
	private int initialBytesToStrip;
	public StringLengthFieldBasedFrameEncoder() {
		// TODO Auto-generated constructor stub
	}
	public void setInitialBytesToStrip(int initialBytesToStrip) {
		this.initialBytesToStrip = initialBytesToStrip;
	}
	public void setLengthAdjustment(int lengthAdjustment) {
		this.lengthAdjustment = lengthAdjustment;
	}
	public void setLengthFieldLength(int lengthFieldLength) {
		this.lengthFieldLength = lengthFieldLength;
	}
	public void setLengthFieldEndOffset(int lengthFieldEndOffset) {
		this.lengthFieldEndOffset = lengthFieldEndOffset;
	}
	public void setLengthFieldOffset(int lengthFieldOffset) {
		this.lengthFieldOffset = lengthFieldOffset;
	}
	public void setMaxFrameLength(int maxFrameLength) {
		this.maxFrameLength = maxFrameLength;
	}
	public StringLengthFieldBasedFrameEncoder(int maxFrameLength,
			int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip, boolean isCharacterLength) {
		if (maxFrameLength <= 0) {
			throw new IllegalArgumentException(
					"maxFrameLength must be a positive integer: "
							+ maxFrameLength);
		}

		if (lengthFieldOffset < 0) {
			throw new IllegalArgumentException(
					"lengthFieldOffset must be a non-negative integer: "
							+ lengthFieldOffset);
		}
		if (initialBytesToStrip < 0) {
			throw new IllegalArgumentException(
					"initialBytesToStrip must be a non-negative integer: "
							+ initialBytesToStrip);
		}
		if (lengthFieldLength != 1 && lengthFieldLength != 2
				&& lengthFieldLength != 3 && lengthFieldLength != 4
				&& lengthFieldLength != 6 && lengthFieldLength != 8) {
			throw new IllegalArgumentException(
					"lengthFieldLength must be either 1, 2, 3, 4, 6, 8: "
							+ lengthFieldLength);
		}

		if (lengthFieldOffset > maxFrameLength - lengthFieldLength) {
			throw new IllegalArgumentException("maxFrameLength ("
					+ maxFrameLength + ") "
					+ "must be equal to or greater than "
					+ "lengthFieldOffset (" + lengthFieldOffset + ") + "
					+ "lengthFieldLength (" + lengthFieldLength + ").");
		}

		this.maxFrameLength = maxFrameLength;
		this.lengthFieldOffset = lengthFieldOffset;
		this.lengthFieldLength = lengthFieldLength;
		this.lengthAdjustment = lengthAdjustment;
		lengthFieldEndOffset = lengthFieldOffset + lengthFieldLength;
		this.initialBytesToStrip = initialBytesToStrip;
		// TODO Auto-generated constructor stub
	}
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (!(msg instanceof byte[])) {
			log.error("msg not byte[]!!!!!");
			return msg;
		}
		byte[] sBytes = (byte [])msg;
		byte[] totalBytes = new byte[sBytes.length + lengthFieldLength - initialBytesToStrip];
		if(totalBytes.length > maxFrameLength){
			log.error("msg length > maxFrameLength");
			return null;
		}
		int totalLength = sBytes.length;
		int lenField = totalBytes.length - lengthFieldEndOffset - lengthAdjustment;
		byte[] lenBytes = MTUtil.toNumberString(lenField, lengthFieldLength).getBytes();
		System.arraycopy(sBytes, initialBytesToStrip, totalBytes, 0, lengthFieldOffset);
		System.arraycopy(lenBytes, 0, totalBytes, lengthFieldOffset, lengthFieldLength);
		System.arraycopy(sBytes, lengthFieldOffset + initialBytesToStrip, totalBytes, lengthFieldEndOffset, sBytes.length-lengthFieldOffset-initialBytesToStrip);
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer(ByteOrder.BIG_ENDIAN,totalBytes.length);
		buf.writeBytes(totalBytes);
		return buf;
	}

}
