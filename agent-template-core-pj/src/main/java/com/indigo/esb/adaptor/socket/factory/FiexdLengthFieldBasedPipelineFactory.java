package com.indigo.esb.adaptor.socket.factory;

import static org.jboss.netty.channel.Channels.pipeline;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelPipeline;

import com.indigo.esb.adaptor.socket.codec.FixedLengthFrameDecoder;
import com.indigo.esb.adaptor.socket.codec.FixedLengthFrameEncoder;


public class FiexdLengthFieldBasedPipelineFactory extends DefaultPipelineFactory {

	private final Log log = LogFactory.getLog(FiexdLengthFieldBasedPipelineFactory.class);

    private int frameLength;
    public void setFrameLength(int frameLength) {
		this.frameLength = frameLength;
	}
    public ChannelPipeline getPipeline() throws Exception {

        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("decoder", new FixedLengthFrameDecoder(frameLength));
        pipeline.addLast("encoder", new FixedLengthFrameEncoder(frameLength));
        Set<String> keySet = getHandlerMap().keySet();
        for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key =  iterator.next();
			pipeline.addLast(key, getHandlerMap().get(key));
		}
        return pipeline;
    }
}
