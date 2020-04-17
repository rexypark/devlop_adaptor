package com.indigo.esb.bmt.tcp.client;

import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import com.indigo.esb.bmt.tcp.handler.ObjectASyncHandler;
import com.mb.mci.common.codec.factory.ObjectDecoderFactory;
import com.mb.mci.common.exception.ConnectionFailException;
import com.mb.mci.common.exception.DisconnectionException;
import com.mb.mci.common.exception.TimeOutException;
import com.mb.mci.pattern.online.context.ClientContext;
import com.mb.mci.pattern.online.manager.DefaultClientManager;
import com.mb.mci.pattern.online.pipeline.factory.StandardChannelPipelineFactory;

/**
 * @author clupine
 *
 */
public class ObjectSyncClient<T> {

	private final Log logger = LogFactory.getLog(ObjectSyncClient.class);

	Channel channel = null;
	DefaultClientManager manager = null;
	ClientContext context = new ClientContext();

	public ObjectSyncClient(String ip, int port) throws ConnectionFailException {

		context.setIp(ip);
		context.setPort(port);
		manager = new DefaultClientManager(context);

		StandardChannelPipelineFactory pipelineFactory = new StandardChannelPipelineFactory();

		LinkedHashMap<String, Object> requestMap = new LinkedHashMap<String, Object>();
		requestMap.put("ObjectDecoderFactory", new ObjectDecoderFactory());
		requestMap.put("ObjectEncoder", new ObjectEncoder(1024));
		requestMap.put("ObjectASyncHandler", new ObjectASyncHandler());
		pipelineFactory.setHandlerMap(requestMap);
		manager.setChannelPipelineFactory(pipelineFactory);
		if (!manager.connect()) {
			throw new ConnectionFailException(ip + ":" + port
					+ " Object TCP Channel Connect Fail");
		}
		this.channel = context.getChannel();
	}

	public void send(final Object reqData) throws ConnectionFailException, DisconnectionException, InterruptedException,
	TimeOutException {
		
			if (channel == null || !channel.isConnected()) {
				logger.debug("Object TCP Channel Disconnected");
				throw new DisconnectionException(context.getIp() + ":" + context.getPort() + " Object TCP Channel Disconnected Fail");
			}
			channel.write(reqData).awaitUninterruptibly().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					logger.info(Thread.currentThread().getId()+" Object DATA Write Complete future : " + future.isSuccess());
				}
			});
		
		
	}

	public void close() {
		if (channel != null) {
			channel.close();
		}
		manager.release();
	}

}
