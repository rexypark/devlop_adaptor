package com.indigo.esb.adaptor.socket.factory;

import static org.jboss.netty.channel.Channels.pipeline;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;

import com.indigo.esb.adaptor.socket.codec.FrameDecoder;
import com.indigo.esb.adaptor.socket.codec.StringLengthFieldBasedFrameEncoder;
import com.indigo.esb.adaptor.socket.codec.TestEncoder;


public class FileServerPipelineFactory extends DefaultPipelineFactory {

	private final Log log = LogFactory.getLog(FileServerPipelineFactory.class);

    private Map<String, ChannelHandler> handlerMap;
    public ChannelPipeline getPipeline() throws Exception {

          ChannelPipeline pipeline = pipeline();

          Set<String> keySet = handlerMap.keySet();

          for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
              String key =  iterator.next();
              pipeline.addLast(key, (ChannelHandler) handlerMap.get(key));
              log.debug("#### put handler : " + key);
          }
          return pipeline;
      }

public void setHandlerMap( Map<String, ChannelHandler> handlerMap ){

      this.handlerMap = handlerMap;
}


}
