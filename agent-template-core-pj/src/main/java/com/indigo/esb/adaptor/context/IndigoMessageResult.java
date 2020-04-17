package com.indigo.esb.adaptor.context;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_RECEIVE_TIME;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_REPLY_QEUEE_NAME;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TARGET_DESTINATION;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;

import com.indigo.esb.config.InterfaceDataType;
import com.indigo.esb.config.InterfaceInfo;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyEnum;
import com.indigo.esb.jms.IndigoHeaderMessagePostProcessor;
import com.indigo.esb.jms.IndigoTemplateMessage;
import com.indigo.indigomq.broker.jmx.IndigoMQObjectMessageBrowsable;

public class IndigoMessageResult {

	protected JmsTemplate jmsTemplate;

	protected InterfaceInfo interfaceInfo;
	protected Message message;
	protected IndigoHeaderMessagePostProcessor properties;
	protected String pocessStatus;
	protected int errCount = 0;
	
	IndigoTemplateMessage templateMessage = null;
	
	public IndigoMessageResult(InterfaceInfo interfaceInfo, JmsTemplate jmsTemplate, Message message) {
		this.interfaceInfo = interfaceInfo;
		this.message = message;
		this.jmsTemplate = jmsTemplate;
		makeProperties(message);
		
		Object pojo = null;
		
		if(jmsTemplate!=null){
			try {
				pojo = jmsTemplate.getMessageConverter().fromMessage(this.message);
			} catch (MessageConversionException e) {
				e.printStackTrace();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		
		this.templateMessage = (IndigoTemplateMessage) pojo;
	}
	
	/**
	 * JMS Properties ->map
	 */
	private void makeProperties(Message receivedMessage) {
		properties = new IndigoHeaderMessagePostProcessor();
		try {
			@SuppressWarnings("unchecked")
			Enumeration<Object> propertyKey = receivedMessage.getPropertyNames();

			while (propertyKey.hasMoreElements()) {
				String key = propertyKey.nextElement().toString();
				if (key.equals(ESB_IF_DATA_TYPE)) {
					properties.addHeaderInfo(ESB_IF_DATA_TYPE, InterfaceDataType.RESULT.name());
				} else {
					properties.addHeaderInfo(key, receivedMessage.getStringProperty(key));
				}
			}
			String receiveTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
			properties.addHeaderInfo(ESB_RECEIVE_TIME, receiveTime);

			if (interfaceInfo.getAddDataMap().containsKey(ESB_RECEIVE_TIME.toUpperCase())) {
				properties
						.addHeaderInfo(interfaceInfo.getAddDataMap().get(ESB_RECEIVE_TIME.toUpperCase()), receiveTime);
			}
			if(!properties.getHeaderInfoMap().containsKey(ESB_REPLY_QEUEE_NAME)){
			    properties.addHeaderInfo(ESB_REPLY_QEUEE_NAME, interfaceInfo.getReturnDestinationName());
			}
			properties.addHeaderInfo(ESB_TARGET_DESTINATION, interfaceInfo.getTargetDestinationName());

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	public InterfaceInfo getInterfaceInfo() {
		return interfaceInfo;
	}

	public Message getMessage() {
		return this.message;
	}

	public Object getDataObj() throws Exception {
		return templateMessage.getDataObj();
	}
	public void setDataObj(Object obj){
		templateMessage.setMessage(obj);
	}

	public IndigoHeaderMessagePostProcessor getProperties() {
		return properties;
	}

	public void addProperty(String key, String value) {
		this.properties.addHeaderInfo(key, value);
	}

	public String getProperty(String key) {
		return this.properties.getHeaderInfoMap().get(key);
	}

	public void setPocessStatus(String pocessStatus) {
		addProperty(ESB_TRANS_STATUS, pocessStatus);
		if (interfaceInfo.getAddDataMap().containsKey(ESB_TRANS_STATUS.toUpperCase())) {
			properties.addHeaderInfo(interfaceInfo.getAddDataMap().get(ESB_TRANS_STATUS.toUpperCase()), pocessStatus);
		}
		this.pocessStatus = pocessStatus;

	}

	public String getPocessStatus() {
		return pocessStatus;
	}

	public IndigoMQObjectMessageBrowsable getTemplateMessage(Object returnObj) {
		return new IndigoTemplateMessage(returnObj);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
