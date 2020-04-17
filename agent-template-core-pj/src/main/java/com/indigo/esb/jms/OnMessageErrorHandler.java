package com.indigo.esb.jms;

import java.util.Map;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.ErrorHandler;

import com.indigo.esb.util.JMSMessageUtil;

public class OnMessageErrorHandler extends JmsTemplate implements ErrorHandler,
		ExceptionListener {

	private static final Log logger = LogFactory
			.getLog(OnMessageErrorHandler.class);

	public OnMessageErrorHandler() {
	}

	@Override
	public void onException(JMSException t) {
		sendError(t);
	}

	@Override
	public void handleError(Throwable t) {
		sendError(t);
	}

	private void sendError(Throwable t) {
		Map<String,Object> prop = null;
		if (t instanceof ContextedRuntimeException) {
			ContextedRuntimeException cre = (ContextedRuntimeException) t;
			Message handledMsg = (Message)cre.getFirstContextValue("message");
			try {
				prop = JMSMessageUtil.getMessageProperties(handledMsg);
			} catch (JMSException e) {
				
			}
		}
		final Map<String,Object> tgtProp = prop;
		final String error = ArrayUtils.toString(ExceptionUtils
				.getRootCauseStackTrace(t)) ;
		logger.error(error);
		send("ONMESSAGE_ERROR", new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				Message msg = session.createTextMessage(error);
				JMSMessageUtil.setMessageProperties(msg, tgtProp);
				return msg;
			}
		});
	}

}
