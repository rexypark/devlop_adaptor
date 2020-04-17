package com.indigo.esb.adaptor.socket.codec;

/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;

public class StringLengthFieldBasedFrameDecoder extends FrameDecoder implements Cloneable {
	private final Log logger = LogFactory
			.getLog(StringLengthFieldBasedFrameDecoder.class);
	private final int maxFrameLength;
	private final int lengthFieldOffset;
	private final int lengthFieldLength;
	private final int lengthFieldEndOffset;
	private final int lengthAdjustment;
	private final int initialBytesToStrip;
	private final boolean isCharacterLength;
	private volatile boolean discardingTooLongFrame;
	private volatile long tooLongFrameLength;
	private volatile long bytesToDiscard;

	/**
	 * Creates a new instance.
	 * 
	 * @param maxFrameLength
	 *            the maximum length of the frame. If the length of the frame is
	 *            greater than this value, {@link TooLongFrameException} will be
	 *            thrown.
	 * @param lengthFieldOffset
	 *            the offset of the length field
	 * @param lengthFieldLength
	 *            the length of the length field
	 * 
	 */
	
	public StringLengthFieldBasedFrameDecoder(int maxFrameLength,
			int lengthFieldOffset, int lengthFieldLength) {
		this(maxFrameLength, lengthFieldOffset, lengthFieldLength, 0, 0, false);
	}

	public StringLengthFieldBasedFrameDecoder(int maxFrameLength,
			int lengthFieldOffset, int lengthFieldLength,
			boolean isCharacterLength) {
		this(maxFrameLength, lengthFieldOffset, lengthFieldLength, 0, 0,
				isCharacterLength);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param maxFrameLength
	 *            the maximum length of the frame. If the length of the frame is
	 *            greater than this value, {@link TooLongFrameException} will be
	 *            thrown.
	 * @param lengthFieldOffset
	 *            the offset of the length field
	 * @param lengthFieldLength
	 *            the length of the length field
	 * @param lengthAdjustment
	 *            the compensation value to add to the value of the length field
	 * @param initialBytesToStrip
	 *            the number of first bytes to strip out from the decoded frame
	 */
	public StringLengthFieldBasedFrameDecoder(int maxFrameLength,
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
		this.isCharacterLength = isCharacterLength;
		lengthFieldEndOffset = lengthFieldOffset + lengthFieldLength;
		this.initialBytesToStrip = initialBytesToStrip;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {

		if (discardingTooLongFrame) {
			long bytesToDiscard = this.bytesToDiscard;
			int localBytesToDiscard = (int) Math.min(bytesToDiscard, buffer
					.readableBytes());
			buffer.skipBytes(localBytesToDiscard);
			bytesToDiscard -= localBytesToDiscard;
			this.bytesToDiscard = bytesToDiscard;
			if (bytesToDiscard == 0) {
				// Reset to the initial state and tell the handlers that
				// the frame was too large.
				discardingTooLongFrame = false;
				long tooLongFrameLength = this.tooLongFrameLength;
				this.tooLongFrameLength = 0;
				throw new TooLongFrameException(
						"Adjusted frame length exceeds " + maxFrameLength
								+ ": " + tooLongFrameLength);
			} else {
				// Keep discarding.
				return null;
			}
		}

		if (buffer.readableBytes() < lengthFieldEndOffset) {
			return null;
		}

		int actualLengthFieldOffset = buffer.readerIndex() + lengthFieldOffset;
		long frameLength;
		String lenStr = null;
		if (isCharacterLength) {
			logger.debug(buffer);
			byte[] dst = new byte[lengthFieldLength];
			buffer.getBytes(actualLengthFieldOffset, dst);
			/*byte[] headBytes = new byte[211];
			buffer.getBytes(0, headBytes);
			logger.info("헤더부:|" + new String(headBytes) + "|");*/
			lenStr = new String(dst);
			frameLength = NumberUtils.toInt(lenStr);
			logger.debug(ctx.getChannel() + ", 전문 길이:'" + lenStr + "':" + frameLength);
		} else {
			switch (lengthFieldLength) {
			case 1:
				frameLength = buffer.getUnsignedByte(actualLengthFieldOffset);
				break;
			case 2:
				frameLength = buffer.getUnsignedShort(actualLengthFieldOffset);
				break;
			case 3:
				frameLength = buffer.getUnsignedMedium(actualLengthFieldOffset);
				break;
			case 4:
				frameLength = buffer.getUnsignedInt(actualLengthFieldOffset);
				break;
			case 8:
				frameLength = buffer.getLong(actualLengthFieldOffset);
				break;
			default:
				throw new Error("should not reach here");
			}
		}

		if (frameLength < 0) {
			buffer.skipBytes(lengthFieldEndOffset);
			throw new CorruptedFrameException(
					"negative pre-adjustment length field: " + frameLength);
		}

		frameLength += lengthAdjustment + lengthFieldEndOffset;
		logger.debug("#### 조정된 frameLength : " + frameLength);
		
		if (frameLength < lengthFieldEndOffset) {
			buffer.skipBytes(lengthFieldEndOffset);
			throw new CorruptedFrameException("수신 받은 MCI헤더의 길이가 잘못되었습니다.['"
					+ lenStr + "':" + frameLength + "]");
			// throw new CorruptedFrameException("Adjusted frame length ("
			// + frameLength + ") is less "
			// + "than lengthFieldEndOffset: " + lengthFieldEndOffset);
		}

		if (frameLength > maxFrameLength) {
			// Enter the discard mode and discard everything received so far.
			discardingTooLongFrame = true;
			tooLongFrameLength = frameLength;
			bytesToDiscard = frameLength - buffer.readableBytes();
			buffer.skipBytes(buffer.readableBytes());
			return null;
		}

		// never overflows because it's less than maxFrameLength
		int frameLengthInt = (int) frameLength;
		if (buffer.readableBytes() < frameLengthInt) {
			return null;
		}

		if (initialBytesToStrip > frameLengthInt) {
			buffer.skipBytes(frameLengthInt);
			throw new CorruptedFrameException("Adjusted frame length ("
					+ frameLength + ") is less " + "than initialBytesToStrip: "
					+ initialBytesToStrip);
		}
		logger.debug(buffer + "==> initialBytesToStrip : " + initialBytesToStrip + ", frameLengthInt : " + frameLengthInt);
		buffer.skipBytes(initialBytesToStrip);
		
		return buffer.readBytes(frameLengthInt - initialBytesToStrip);
	}
}
