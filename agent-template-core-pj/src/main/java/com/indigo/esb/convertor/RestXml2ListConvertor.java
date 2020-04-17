package com.indigo.esb.convertor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.util.XmlUtil4Dom4j;

/**
 * @author clupine-mb
 *
 */
public class RestXml2ListConvertor implements
		ESBListConvertor<Map<String, Object>> {
	protected final static Logger logger = LoggerFactory
			.getLogger("RestXml2ListConvertor");

	String itemsXpath = "//body/items/item";

	public void setItemsXpath(String itemsXpath) {
		this.itemsXpath = itemsXpath;
	}

	@Override
	public List<Map<String, Object>> convert(Object data) throws Exception {
		List<Map<String, Object>> convertList = new ArrayList<Map<String, Object>>();
		Document doc = XmlUtil4Dom4j.parseDoc((String) data, "utf-8");
		List<Node> itemList = doc.selectNodes(itemsXpath);
		logger.debug("itemList = {} ", itemList.size());
		for (Node item : itemList) {
			Map rowMap = XmlUtil4Dom4j.xml2Mapping(item.asXML());
			logger.debug("item : {} ", rowMap);
			convertList.add(rowMap);
		}
		return convertList;
	}

	public static void main(String[] args) throws Exception {

//		String serviceKey = "7wN73nJrxhUgLp4Z927RGjhTqdoGFAS3ASWb9ODH/W0brHgxyRLom1eST15xFdYI7RPTF6xruKFufpcARzbERw==";
//		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
//		paramList.add(new BasicNameValuePair("ServiceKey", serviceKey));
//		paramList.add(new BasicNameValuePair("from_date", "20141022"));
//		paramList.add(new BasicNameValuePair("to_date", "20141022"));
//		paramList.add(new BasicNameValuePair("numOfRows", "10000")); // 최대 만건까지
//		// paramList.add(new BasicNameValuePair("lclass_name", "));
//		// paramList.add(new BasicNameValuePair("mclass_name", ));
//		// paramList.add(new BasicNameValuePair("sclass_name", "));
//		// ServiceKey=7wN73nJrxhUgLp4Z927RGjhTqdoGFAS3ASWb9ODH%2FW0brHgxyRLom1eST15xFdYI7RPTF6xruKFufpcARzbERw%3D%3D&from_date=20141020&to_date=20141030
//		String url = "http://openapi.epis.or.kr/openapi/service/InsttExaminPcService/getGarakDelngInfoList";
//		String queryStr = URLEncodedUtils.format(paramList, "utf-8");
//		logger.info("parameter : {}", queryStr);
//		HttpGet request = new HttpGet(url + "?" + queryStr);
//		HttpClient client = new DefaultHttpClient();
//		HttpResponse response = client.execute(request);
//		HttpEntity entity = response.getEntity();
//
//		String xml = null;
//		if (entity != null) {
//			xml = IOUtils.toString(entity.getContent());
//		}
//
//		// logger.info("resultXml src  : {} ", xml);
//		Document doc = XmlUtil4Dom4j.parseDoc(xml, "utf-8");
//		// logger.info("resultXml : {} ", print(doc));
//
//		List<Node> itemList = doc.selectNodes("//body/items/item");
//		logger.info("itemList = {} ", itemList.size());
//		for (Node item : itemList) {
//			// item.
//			Map rowMap = XmlUtil4Dom4j.xml2Mapping(item.asXML());
//			logger.info("item : {} ", rowMap);
//
//		}

	}

}
