package com.indigo.esb.adaptor.context;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_ID;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_TYPE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_RECEIVE_SYSEM_ID;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_REPLY_QEUEE_NAME;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_SYSTEM_ID;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateFormatUtils;

import com.indigo.esb.config.InterfaceDataType;
import com.indigo.esb.config.InterfaceInfo;
import com.indigo.esb.config.InterfaceType;
import com.indigo.esb.jms.IndigoHeaderMessagePostProcessor;
import com.indigo.esb.jms.IndigoTemplateMessage;
import com.indigo.indigomq.broker.jmx.IndigoMQObjectMessageBrowsable;

public class IndigoSignalResult {

	protected InterfaceInfo interfaceInfo;

	protected IndigoMQObjectMessageBrowsable templateMessage;
	protected Object pollResultDataObj;
	protected Object attchObject;

	public void setAttchObject(Object attchObject) {
		this.attchObject = attchObject;
	}
	
	public Object getAttchObject() {
		return attchObject;
	}
	
	public void setProperties(IndigoHeaderMessagePostProcessor properties) {
		this.properties = properties;
	}
	
	protected IndigoHeaderMessagePostProcessor properties;

	public IndigoSignalResult(InterfaceInfo interfaceInfo) {
		this.interfaceInfo = interfaceInfo;
		makeProperties();
	}

	public IndigoSignalResult(InterfaceInfo interfaceInfo, byte[] data) {
		this.interfaceInfo = interfaceInfo;
		makeProperties(data);
	}

	public void setInterfaceInfo(InterfaceInfo interfaceInfo) {
		this.interfaceInfo = interfaceInfo;
	}

	/**
	 * Log Trace IMS 기록을 위한 Properties Setting
	 */
	public void makeProperties() {
		String tx_id = interfaceInfo.getTxidGenerator().create();
		String initTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");

		properties = new IndigoHeaderMessagePostProcessor();
		properties.addHeaderInfo(ESB_IF_ID, interfaceInfo.getInterfaceId());
		properties.addHeaderInfo(ESB_IF_TYPE, interfaceInfo.getInterfaceType().name());
		properties.addHeaderInfo(ESB_IF_DATA_TYPE, InterfaceDataType.DATA.name());
		properties.addHeaderInfo(ESB_TX_ID, tx_id);
		if (interfaceInfo.getAddDataMap().containsKey(ESB_TX_ID)) {
			tx_id = interfaceInfo.getAddDataMap().get(ESB_TX_ID);
			properties.addHeaderInfo(ESB_TX_ID, tx_id);
			properties.addHeaderInfo(ESB_REPLY_QEUEE_NAME, interfaceInfo.getAddDataMap().get(ESB_REPLY_QEUEE_NAME));
		}
		properties.addHeaderInfo(ESB_INIT_TIME, initTime);
		// tx_id 컬럼값으로 Data 추가
		if (interfaceInfo.getAddDataMap().containsKey(ESB_TX_ID.toUpperCase())) {
			properties.addHeaderInfo(interfaceInfo.getAddDataMap().get(ESB_TX_ID.toUpperCase()), tx_id);
		}
		// init_time 컬럼값으로 Data 추가
		if (interfaceInfo.getAddDataMap().containsKey(ESB_INIT_TIME.toUpperCase())) {
			properties.addHeaderInfo(interfaceInfo.getAddDataMap().get(ESB_INIT_TIME.toUpperCase()), initTime);
		}
		properties.addHeaderInfo(ESB_SEND_SYSTEM_ID, interfaceInfo.getSndSysId());
		properties.addHeaderInfo(ESB_RECEIVE_SYSEM_ID, interfaceInfo.getRcvSysId());
		properties.addHeaderInfo(ESB_SEND_ROW_COUNT, "0");
	}

	public void makeProperties(byte[] data) {
		String tx_id = interfaceInfo.getTxidGenerator().create();
		String initTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");

		properties = new IndigoHeaderMessagePostProcessor();
		properties.addHeaderInfo(ESB_IF_ID, interfaceInfo.getInterfaceId());
		properties.addHeaderInfo(ESB_IF_TYPE, InterfaceType.TP.name());
		properties.addHeaderInfo(ESB_IF_DATA_TYPE, InterfaceDataType.DATA.name());
		properties.addHeaderInfo(ESB_TX_ID, tx_id);
		properties.addHeaderInfo(ESB_INIT_TIME, initTime);
		properties.addHeaderInfo(ESB_SEND_SYSTEM_ID, interfaceInfo.getSndSysId());
		properties.addHeaderInfo(ESB_RECEIVE_SYSEM_ID, interfaceInfo.getRcvSysId());
		properties.addHeaderInfo(ESB_SEND_ROW_COUNT, "0");
		Map<String, String> dataMap = interfaceInfo.getAddDataMap();
		if (dataMap == null)
			return;
		for (Iterator iterator = dataMap.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String[] info = dataMap.get(key).split(",");
			if (info.length == 1) {
				properties.addHeaderInfo(key, dataMap.get(key));
			} else if (info.length == 2) {
				String value = new String(data, Integer.parseInt(info[0]), Integer.parseInt(info[1]));
				properties.addHeaderInfo(key, value);
			} else if (info.length == 6) {
				String value = new String(data, Integer.parseInt(info[1]), Integer.parseInt(info[2]));
				if (value.equals(info[3])) {
					value = info[4];
				} else {
					value = info[5];
				}
				properties.addHeaderInfo(key, value);
			}
		}
	}

	public InterfaceInfo getInterfaceInfo() {
		return interfaceInfo;
	}

	public IndigoHeaderMessagePostProcessor getProperties() {
		return this.properties;
	}

	public void addProperty(String key, String value) {
		this.properties.addHeaderInfo(key, value);
	}

	public String getProperty(String key) {
		return this.properties.getHeaderInfoMap().get(key);
	}

	public IndigoMQObjectMessageBrowsable getTemplateMessage() {
		return new IndigoTemplateMessage(getPollResultDataObj());
	}

	public Object getPollResultDataObj() {
		return pollResultDataObj;
	}

	public void setPollResultDataObj(Object pollResultDataObj) {
		this.pollResultDataObj = pollResultDataObj;
	}

	public void setResultCount(int cnt) {
		this.properties.addHeaderInfo(ESB_SEND_ROW_COUNT, "" + cnt);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
