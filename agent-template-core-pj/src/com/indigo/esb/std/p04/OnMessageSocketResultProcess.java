package com.indigo.esb.std.p04;

import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.socket.OnMessageSocketSupport;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.util.OnlineMTMessageIDCache;
public class OnMessageSocketResultProcess extends OnMessageSocketSupport {

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		String ifId = null;
		String txid = null;
		String svcName = null;
		try {
			responseMessage(indigoMessageResult);
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			
			// TODO: handle exception
		}
	}
	
}
