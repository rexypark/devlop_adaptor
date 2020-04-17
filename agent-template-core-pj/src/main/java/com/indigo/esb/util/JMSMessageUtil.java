package com.indigo.esb.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JMSMessageUtil {

	private static final Log logger = LogFactory.getLog(JMSMessageUtil.class);

	public static String getStringProperty(Message message, String key) {
		String value = null;
		try {
			value = message.getStringProperty(key);
		} catch (JMSException e) {
			logger.error("", e);
			throw new IllegalArgumentException(e);
		}
		return value;
	}

	public static String getResultDestinationName(String dataDestinationName) {
		String value = dataDestinationName.replaceFirst("_DATA", "_RSLT");
		return value;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMessageProperties(Message msg) throws JMSException {
		Map<String, Object> properties = new HashMap<String, Object>();
		Enumeration<String> srcProperties = msg.getPropertyNames();
		while (srcProperties.hasMoreElements()) {
			String propertyName = srcProperties.nextElement();
			properties.put(propertyName, msg.getObjectProperty(propertyName));
		}
		return properties;
	}

	public static void setMessageProperties(Message msg, Map<String, Object> proporties) throws JMSException {
		if (proporties == null) {
			return;
		}
		for (Map.Entry<String, Object> entry : proporties.entrySet()) {
			String propertyName = entry.getKey();
			Object value = entry.getValue();
			msg.setObjectProperty(propertyName, value);
		}
	}

}
