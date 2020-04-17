package com.indigo.esb.adaptor.socket.codec;

import java.nio.ByteOrder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.indigo.esb.util.MTUtil;

public class DefaultEncoder extends OneToOneEncoder{

      private Log log = LogFactory.getLog(getClass());

      @Override
      protected Object encode( ChannelHandlerContext ctx, Channel channel, Object msg ) throws Exception{

            // TODO Auto-generated method stub

            log.info("################### ¸Þ½ÃÁö Encode");
            byte[] bytes = (byte[]) msg;
            byte[] totalBytes = new byte[bytes.length];
            ChannelBuffer buf = ChannelBuffers.dynamicBuffer(ByteOrder.BIG_ENDIAN, totalBytes.length);
            buf.writeBytes(bytes);
            return buf;
      }

}
