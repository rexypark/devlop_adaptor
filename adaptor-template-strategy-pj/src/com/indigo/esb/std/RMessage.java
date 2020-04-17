package com.indigo.esb.std;

import java.io.Serializable;
import java.util.Map;

public class RMessage implements Serializable {

	Map<String, Object> header;
	Serializable data;

	public Map<String, Object> getHeader() {
		return header;
	}

	public void setHeader(Map<String, Object> header) {
		this.header = header;
	}

	public Serializable getData() {
		return data;
	}

	public void setData(Serializable data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "RMessage [header=" + header + ", data=" + data + "]";
	}

}
