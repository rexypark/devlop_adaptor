package com.indigo.esb.std.p04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.socket.client.DefaultSocketClientHandler;
import com.indigo.esb.config.InterfaceDataType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.jms.IndigoHeaderMessagePostProcessor;
import com.indigo.esb.util.OnlineMTMessageIDCache;
/**
 * 
 * TCP 서버로부터 수신한 메시지를 처리하는 CLASS
 *
 */
public class ClientMessageHandler extends DefaultSocketClientHandler {

	private final Logger log = Logger.getLogger(this.getClass());
	private JmsTemplate jmsTemplate;

	public ClientMessageHandler() {
		log.info("###### version : 1.03 2010-08-10");
	}

	/**
	 * 
	 */
	@Override
	public IndigoSignalResult onMessage(IndigoSignalResult result) {
		// TODO Auto-generated method stub
		result = makeReturnMessage(result);
		return super.onMessage(result);
	}

}
