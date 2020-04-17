package com.indigo.esb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.util.HashMap;
//import java.util.HashMap;

public class XmlUtils {
	public static String makeXmlMessage(String body) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"euc-kr\"?>\n");
		sb.append(makeDataToXml(body, "root"));
		return sb.toString();
	}

	public static String makeXmlMessage(HashMap<String, Object> header, String body) {
		StringBuffer sb = new StringBuffer();
		sb.append(makeDataToXml(header, "sndheader"));
		sb.append(makeDataToXml(body, "sndbizdata"));
		return makeXmlMessage(sb.toString());
	}

	/*
	 * public static String makeXmlMessage(HashMap<String, String> header,
	 * List<HashMap<String, Object>> bodyList) { StringBuffer sb = new
	 * StringBuffer(); sb.append(makeDataToXml(header, "sndheader"));
	 * sb.append(makeDataListToXml(bodyList, "sndbizdata", "rows")); return
	 * makeXmlMessage(sb.toString()); }
	 */

	public static String makeXmlMessage(HashMap<String, Object> header, List<HashMap<String, Object>> bodyList) {
		StringBuffer sb = new StringBuffer();
		sb.append(makeDataToXml(header, "sndheader"));
		sb.append(makeDataListToXml(bodyList, "sndbizdata", "rows"));
		return makeXmlMessage(sb.toString());
	}

	public static String makeDataListToXml(HashMap<String, Object> dataMap, String hubTagName, String hubTagAttribute,
			String tagName) {
		ArrayList<HashMap<String, Object>> dataMapList = new ArrayList<HashMap<String, Object>>();
		dataMapList.add(dataMap);
		return makeDataListToXml(dataMapList, hubTagName, hubTagAttribute, tagName);
	}

	public static String makeDataListToXml(List<HashMap<String, Object>> listMap, String tagName) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < listMap.size(); ++i) {
			sb.append(makeDataToXml((HashMap<String, Object>) listMap.get(i), tagName));
		}
		return sb.toString();
	}

	public static String makeDataListToXml(List<HashMap<String, Object>> listMap, String hubTagName, String tagName) {
		StringBuffer sb = new StringBuffer();
		sb.append("<" + hubTagName.toLowerCase() + ">\r\n");
		/*
		for(HashMap map : listMap){
			System.out.println(map);
		}
		*/
		sb.append(makeDataListToXml(listMap, tagName));
		sb.append("</" + hubTagName.toLowerCase() + ">\r\n");
		return sb.toString();
	}

	public static String makeDataListToXml(List<HashMap<String, Object>> listMap, String hubTagName,
			String hubTagAttribute, String tagName) {
		StringBuffer result = new StringBuffer();

		result.append("<" + hubTagName.toLowerCase() + " " + hubTagAttribute + ">\r\n");
		result.append(makeDataListToXml(listMap, tagName));
		result.append("</" + hubTagName.toLowerCase() + ">\r\n");

		return result.toString();
	}

	public static String makeDataToXml(String data, String tagName) {
		StringBuffer sb = new StringBuffer();
		sb.append("<" + tagName.toLowerCase() + ">\r\n");
		sb.append(data);
		sb.append("</" + tagName.toLowerCase() + ">\r\n");
		return sb.toString();
	}

	public static String makeDataToXml(Map<String, String> map) {
		StringBuffer result = new StringBuffer();
		Iterator<String> it = map.keySet().iterator();

		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) map.get(key);
			result.append("<" + key + ">" + value + "</" + key + ">\r\n");
		}
		return result.toString();
	}

	public static String makeDataToXml(Map<String, String> map, String tagName) {
		StringBuffer result = new StringBuffer();

		result.append("<" + tagName.toLowerCase() + ">\r\n");
		result.append(makeDataToXml(map));
		result.append("</" + tagName.toLowerCase() + ">\r\n");

		return result.toString();
	}

	public static String makeDataToXml(HashMap<String, Object> map) {
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = map.keySet().iterator();

		while (it.hasNext()) {
			String key = (String) it.next();
			String value = map.get(key).toString();
			sb.append("<" + key + ">" + value + "</" + key + ">");
		}
		return sb.toString();
	}

	public static String makeDataToXml(HashMap<String, Object> map, String tagName) {
		StringBuffer sb = new StringBuffer();
		sb.append("<" + tagName + ">\r\n");
		sb.append(makeDataToXml(map));
		sb.append("</" + tagName + ">\r\n");
		return sb.toString();
	}

	public static String makeErrorXmlMessage(HashMap<String, Object> header,
			List<HashMap<String, Object>> errHeaderList, List<HashMap<String, Object>> errBodyList) {
		StringBuffer sb = new StringBuffer();
		sb.append(makeDataToXml(makeDataToXml(header) + makeDataListToXml(errHeaderList, "err_list", "err"),
				"sndheader"));
		sb.append(makeDataListToXml(errBodyList, "sndbizdata", "rows"));
		return makeXmlMessage(sb.toString());
	}
}