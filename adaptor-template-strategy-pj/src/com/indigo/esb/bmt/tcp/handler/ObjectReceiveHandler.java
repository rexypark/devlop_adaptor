package com.indigo.esb.bmt.tcp.handler;

import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author clupine
 *
 */
public class ObjectReceiveHandler<T> extends SimpleChannelHandler {

	private static Logger logger = LoggerFactory.getLogger(ObjectReceiveHandler.class);

	@Resource
	LinkedBlockingQueue outBoundQueue;
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		logger.info("연결"+e.getChannel());
		super.channelConnected(ctx, e);
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		logger.info("닫힘"+e.getChannel());
		super.channelClosed(ctx, e);
	}
	
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		T data = (T) e.getMessage();
		outBoundQueue.offer(data);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.error("Unexpected exception from downstream.", e.getCause());
		e.getChannel().close();
	}

}
