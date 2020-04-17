package com.indigo.esb.adaptor.socket.codec;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;


public class TestEncoder  extends OneToOneEncoder {

      @Override
      protected Object encode( ChannelHandlerContext ctx, Channel channel, Object msg ) throws Exception{

            // TODO Auto-generated method stub
            return msg;
      }

}
