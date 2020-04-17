package com.indigo.esb.bmt.tcp.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author clupine
 *
 */
public class ObjectASyncHandler extends SimpleChannelHandler {
	
	private static Logger logger = LoggerFactory.getLogger(ObjectASyncHandler.class);
	

	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.error("Unexpected exception from downstream.",	e.getCause());
		e.getChannel().close();
	}
}
