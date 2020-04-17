package com.indigo.esb.std.onsignal;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.std.IndigoCustomAdaptor;

public class CallStartListener implements MessageListener {

	IndigoCustomAdaptor adaptor;
	
	public void setAdaptor(IndigoCustomAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public void onMessage(Message msg) {
		
		if(!(msg instanceof TextMessage)){
			logger.info("CallStartListener MessageType Error  : " + msg);
			return;
		}
		
		String ifid = null;
		try {
			ifid = msg.getStringProperty("if_id");
		} catch (JMSException e) {
			logger.error("Message Properties Error , "  , e);
		}

		
		logger.info("{} Call Start Interface ID : " + ifid);
		adaptor.onSignal(ifid);
		logger.info("{} Call End Interface ID : " + ifid);
	}

}
