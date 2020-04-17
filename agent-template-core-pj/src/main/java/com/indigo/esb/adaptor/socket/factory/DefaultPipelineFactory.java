package com.indigo.esb.adaptor.socket.factory;

import static org.jboss.netty.channel.Channels.pipeline;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.springframework.beans.factory.InitializingBean;

import com.indigo.esb.adaptor.socket.codec.DefaultDecoder;
import com.indigo.esb.adaptor.socket.codec.DefaultEncoder;
import com.indigo.esb.adaptor.socket.codec.StringLengthFieldBasedFrameDecoder;
import com.indigo.esb.adaptor.socket.codec.StringLengthFieldBasedFrameEncoder;
import com.indigo.esb.config.InterfaceInfo;

public class DefaultPipelineFactory implements ChannelPipelineFactory{
	private Map<String, ChannelHandler> handlerMap;
	public void setHandlerMap(Map<String, ChannelHandler> handlerMap) {
		this.handlerMap = handlerMap;
	}
	public Map<String, ChannelHandler> getHandlerMap() {
		return handlerMap;
	}
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		// TODO Auto-generated method stub
	        ChannelPipeline pipeline = pipeline();
	        Set<String> keySet = handlerMap.keySet();

	        pipeline.addLast("decoder", new DefaultDecoder());
	        pipeline.addLast("encoder", new DefaultEncoder());


	        for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
	            String key =  iterator.next();
	            pipeline.addLast(key, (ChannelHandler) handlerMap.get(key));
	            ////log.debug("#### put handler : " + key);
	        }
		return pipeline;
	}
}
