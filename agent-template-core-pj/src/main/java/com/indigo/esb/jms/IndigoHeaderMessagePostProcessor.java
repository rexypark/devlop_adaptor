package com.indigo.esb.jms;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.jms.core.MessagePostProcessor;

public class IndigoHeaderMessagePostProcessor implements MessagePostProcessor {

	Map<String, String> headerInfoMap = new LinkedHashMap<String, String>();

	Message jmsMessage;

	public IndigoHeaderMessagePostProcessor() {
	}

	public IndigoHeaderMessagePostProcessor(Map<String, String> headerInfoMap) {
		this.headerInfoMap = headerInfoMap;
	}

	public Map<String, String> getHeaderInfoMap() {
		return headerInfoMap;
	}

	public void setHeaderInfoMap(Map<String, String> headerInfoMap) {
		this.headerInfoMap = headerInfoMap;
	}

	@Override
	public Message postProcessMessage(Message message) throws JMSException {
		for (Map.Entry<String, String> entry : headerInfoMap.entrySet()) {
			message.setStringProperty(entry.getKey(), entry.getValue());
		}
		jmsMessage = message;
		return message;
	}

	public Message getJmsMessage() {
		return jmsMessage;
	}

	public void addHeaderInfo(String headerKey, String headerValue) {
		this.headerInfoMap.put(headerKey, headerValue);
	}

	public void addHeaderInfo(IndigoHeaderJMSPropertyEnum headerKey, String headerValue) {
		this.headerInfoMap.put(headerKey.toString(), headerValue);
	}

}
