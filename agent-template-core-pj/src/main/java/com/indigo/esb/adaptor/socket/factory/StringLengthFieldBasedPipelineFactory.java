package com.indigo.esb.adaptor.socket.factory;

import static org.jboss.netty.channel.Channels.pipeline;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.indigo.esb.adaptor.socket.codec.DefaultEncoder;
import com.indigo.esb.adaptor.socket.codec.StringLengthFieldBasedFrameDecoder;
import com.indigo.esb.adaptor.socket.codec.StringLengthFieldBasedFrameEncoder;


public class StringLengthFieldBasedPipelineFactory extends DefaultPipelineFactory {

	private final Log log = LogFactory.getLog(StringLengthFieldBasedPipelineFactory.class);

    private Map<String, ChannelHandler> handlerMap;

    private int maxFrameLength;
    private int lengthFieldOffset;//0 //TRAN_LEN 시작지점
    private int lengthFieldLength;//6 //TRAN_LEN 길이
    private int lengthAdjustment;//0  //
    private int initialBytesToStrip; //전문을 어디서부터 끊을것인가
    private boolean isCharacterLength=true; //String : true , Integer : false

	public void setMaxFrameLength(int maxFrameLength) {
		this.maxFrameLength = maxFrameLength;
	}

	public void setLengthFieldOffset(int lengthFieldOffset) {
		this.lengthFieldOffset = lengthFieldOffset;
	}

	public void setLengthFieldLength(int lengthFieldLength) {
		this.lengthFieldLength = lengthFieldLength;
	}

	public void setLengthAdjustment(int lengthAdjustment) {
		this.lengthAdjustment = lengthAdjustment;
	}

	public void setInitialBytesToStrip(int initialBytesToStrip) {
		this.initialBytesToStrip = initialBytesToStrip;
	}

	public void setCharacterLength(boolean isCharacterLength) {
		this.isCharacterLength = isCharacterLength;
	}

	public void setHandlerMap(Map<String, ChannelHandler> handlerMap) {
		this.handlerMap = handlerMap;
	}
	public ChannelPipeline getPipeline() throws Exception {

        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("decoder", new StringLengthFieldBasedFrameDecoder(maxFrameLength,lengthFieldOffset,lengthFieldLength,lengthAdjustment,initialBytesToStrip,isCharacterLength));
        pipeline.addLast("encoder", new DefaultEncoder());
        //pipeline.addLast("encoder", new StringLengthFieldBasedFrameEncoder(maxFrameLength,lengthFieldOffset,lengthFieldLength,lengthAdjustment,initialBytesToStrip,isCharacterLength));
        Set<String> keySet = handlerMap.keySet();

        for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key =  iterator.next();
			pipeline.addLast(key, (ChannelHandler) handlerMap.get(key));
			log.debug("#### put handler : " + key);
		}
        return pipeline;
    }
}
