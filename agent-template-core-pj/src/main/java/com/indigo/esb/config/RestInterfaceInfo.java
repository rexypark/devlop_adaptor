package com.indigo.esb.config;

import java.util.List;

import org.apache.http.NameValuePair;

import com.indigo.esb.convertor.ESBListConvertor;
import com.indigo.esb.generator.ESBValueGenerator;

public class RestInterfaceInfo<T> extends InterfaceInfo {

	List<List<NameValuePair>> paramList;

	String encoding = "euc-kr";

	protected ESBListConvertor<T> listConvertor;
	
	String url;

	String restType = "xml";

	public ESBListConvertor<T> getListConvertor() {
		return listConvertor;
	}

	public void setListConvertor(ESBListConvertor<T> listConvertor) {
		this.listConvertor = listConvertor;
	}

	public String getRestType() {
		return restType;
	}

	public void setRestType(String restType) {
		this.restType = restType;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ESBValueGenerator getTxidGenerator() {
		return txidGenerator;
	}

	public void setTxidGenerator(ESBValueGenerator txidGenerator) {
		this.txidGenerator = txidGenerator;
	}

	public List<List<NameValuePair>> getParamList() {
		return paramList;
	}

	public void setParamList(List<List<NameValuePair>> paramList) {
		this.paramList = paramList;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		setInterfaceType(InterfaceType.REST);
	}

	@Override
	public String toString() {
		return "RestInterfaceInfo [paramList=" + paramList + ", encoding="
				+ encoding + ", txidGenerator=" + txidGenerator + ", url="
				+ url + "]";
	}

}
