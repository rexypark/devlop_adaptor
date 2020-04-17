package com.indigo.esb.signal;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.*;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.indigo.esb.config.InterfaceInfo;
import com.indigo.esb.util.JMSMessageUtil;

public class OnSignalErrorJobListener implements JobListener {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected JmsTemplate jmsTemplate;
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	@Override
	public String getName() {
		return "OnSignalErrorJobListener";
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		if (jobException != null)
			sendError(jobException);
	}

	private void sendError(Throwable t) {
		Map<String, Object> prop = new HashMap<String, Object>();
		if (t instanceof ContextedRuntimeException) {
			ContextedRuntimeException cre = (ContextedRuntimeException) t;
			InterfaceInfo ifInfo = (InterfaceInfo) cre.getFirstContextValue("interfaceInfo");
			prop.put(ESB_IF_ID, ifInfo.getInterfaceId());
			prop.put(ESB_IF_TYPE, ifInfo.getInterfaceType());
			// prop.put(ESB_SOURCE_ADAPTOR_NAME, ifInfo.getAdaptor().getAdaptorName());
		}
		final Map<String, Object> tgtProp = prop;
		final String error = ArrayUtils.toString(ExceptionUtils.getRootCauseStackTrace(t));
		logger.error(error);
		if (jmsTemplate != null) {
			jmsTemplate.send("ONSIGNAL_ERROR", new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					Message msg = session.createTextMessage(error);
					JMSMessageUtil.setMessageProperties(msg, tgtProp);
					return msg;
				}
			});
		}
	}

}
