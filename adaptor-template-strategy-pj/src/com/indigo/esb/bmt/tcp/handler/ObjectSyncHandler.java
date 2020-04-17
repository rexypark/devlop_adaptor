package com.indigo.esb.bmt.tcp.handler;

import java.util.concurrent.BlockingQueue;

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
public class ObjectSyncHandler<T> extends SimpleChannelHandler {
	
	private static Logger logger = LoggerFactory.getLogger(ObjectSyncHandler.class);
	
	
	BlockingQueue<T> queue;
	
	public ObjectSyncHandler(BlockingQueue<T> queue) {
		this.queue = queue;
	}
	


	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		queue.offer((T)e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.error("Unexpected exception from downstream.",	e.getCause());
		e.getChannel().close();
	}
}
