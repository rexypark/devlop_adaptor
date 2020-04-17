package com.indigo.esb.jms;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.indigo.indigomq.broker.jmx.IndigoMQObjectMessageBrowsable;

public class IndigoTemplateMessage implements IndigoMQObjectMessageBrowsable, Serializable {

	public static final long serialVersionUID = 1L;
	private Object dataObj;

	public IndigoTemplateMessage(Object dataObj) {
		setMessage(dataObj);
	}

	public Object getDataObj() {
		return this.dataObj;
	}

	public void setMessage(Object messageObj) {
		this.dataObj = messageObj;
	}

	@Override
	public String toBrowseString() {
		if (dataObj != null) {
			if (dataObj instanceof List) {
				return ((List<?>) dataObj).toString().replace("[", "").replace("]", "").replace("}, ", "}\n");
			} else if (dataObj instanceof String) {
				return ((String) dataObj).toString();
			} else if (dataObj instanceof Map) {
				return ((Map<?, ?>) dataObj).toString();
			} else if(dataObj instanceof byte[]){
				return "Byte Array Data, size : " + ((byte[])dataObj).length;
			}else {
				return "정의 되지 않은 메시지";
			}

		} else {
			return "해당 객체가 null 입니다.";
		}

	}

}
