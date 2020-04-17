package com.indigo.esb.bmt.tcp.message;

import java.io.Serializable;
import java.util.Map;

public class TransferObject implements Serializable{
	
	Map<String,String> headerMap;
	
	Object data;
	
	public Map<String, String> getHeaderMap() {
		return headerMap;
	}

	public void setHeaderMap(Map<String, String> headerMap) {
		this.headerMap = headerMap;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
 
	@Override
	public String toString() {
		return "TransferObject [headerMap=" + headerMap + ", data=" + data + "]";
	}
	
	
	
	
	
	

}
