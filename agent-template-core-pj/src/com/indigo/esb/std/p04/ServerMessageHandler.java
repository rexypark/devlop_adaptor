package com.indigo.esb.std.p04;

import org.jboss.netty.channel.MessageEvent;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.socket.server.DefaultSocketServerHandler;

public class ServerMessageHandler extends DefaultSocketServerHandler {
	@Override
	public IndigoSignalResult onMessage(IndigoSignalResult result, MessageEvent e) throws Exception {
		// TODO Auto-generated method stub
		registMessage(result, e.getChannel());
		
		return super.onMessage(result, e);
	}
}
