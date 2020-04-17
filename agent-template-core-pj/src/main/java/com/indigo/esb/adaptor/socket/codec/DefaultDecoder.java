package com.indigo.esb.adaptor.socket.codec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;


public class DefaultDecoder extends OneToOneDecoder{

      private Log log = LogFactory.getLog(getClass());
      @Override
      protected Object decode( ChannelHandlerContext ctx, Channel channel, Object msg ) throws Exception{

            log.info("################### ¸Þ½ÃÁö Decode");
            // TODO Auto-generated method stub
            ChannelBuffer buffer = (ChannelBuffer)msg;
            return buffer;
      }
}
