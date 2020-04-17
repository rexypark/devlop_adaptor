package com.indigo.esb.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * XPath 관련 유틸 $Id: XPathUtils.java,v 1.2 2012/02/14 00:31:43 seo0859 Exp $
 * 
 */
public class XPathUtils {

	private static DocumentBuilder getDocumentBuilder() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder();
	}

	/**
	 * XML 파일을 Document 로 변환
	 * 
	 * @param xmlFile
	 *            전체 파일 경로
	 * @return XML Document
	 * @throws Exception
	 */
	public static Document convertFileToXmlDoc(File xmlFile) throws Exception {

		return getDocumentBuilder().parse(xmlFile);
	}

	/**
	 * XML String 을 Document 로 변환
	 * 
	 * @param xmlStr
	 *            XML String
	 * @return XML Document
	 * @throws Exception
	 */
	public static Document convertTextToXmlDoc(String xmlStr) throws Exception {

		return getDocumentBuilder().parse(new InputSource(new StringReader(xmlStr)));
	}

	/**
	 * XML 에서 최상위 한 건의 데이터만 조회한다.
	 * 
	 * @param xmlStr
	 * @param xpath
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> getHashMapFromXml(String xmlStr, String xpath) throws Exception {
		Document doc = convertTextToXmlDoc(xmlStr);
		return getHashMapFromXml(doc, xpath);
	}

	/**
	 * XML 에서 최상위 한 건의 데이터만 조회한다.
	 * 
	 * @param xmlStr
	 * @param xpath
	 * @param prefixSplit
	 *            : 접두어 삭제 유무
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> getHashMapFromXml(String xmlStr, String xpath, boolean prefixSplit)
			throws Exception {
		Document doc = convertTextToXmlDoc(xmlStr);
		return getHashMapFromXml(doc, xpath, prefixSplit);
	}

	/**
	 * XML 에서 최상위 한 건의 데이터만 조회한다.
	 * 
	 * @param doc
	 * @param xpath
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> getHashMapFromXml(Document doc, String xpath) throws Exception {
		List<HashMap<String, String>> list = getListHashMapFromXml(doc, xpath);
		if (list.size() == 0)
			return new HashMap<String, String>();
		return list.get(0);
	}

	/**
	 * XML 에서 최상위 한 건의 데이터만 조회한다.
	 * 
	 * @param doc
	 * @param xpath
	 * @param prefixSplit
	 *            : 접두어 삭제 유무
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> getHashMapFromXml(Document doc, String xpath, boolean prefixSplit)
			throws Exception {
		List<HashMap<String, String>> list = getListHashMapFromXml(doc, xpath, prefixSplit);
		if (list.size() == 0)
			return new HashMap<String, String>();
		return list.get(0);
	}

	/**
	 * document 로부터 key = value 값을 조회한다.
	 * 
	 * @param xmlStr
	 * @param xpath
	 * @return
	 * @throws Exception
	 */
	public static List<HashMap<String, String>> getListHashMapFromXml(String xmlStr, String xpath) throws Exception {
		Document doc = convertTextToXmlDoc(xmlStr);
		return getListHashMapFromXml(doc, xpath);
	}

	/**
	 * document 로부터 key = value 값을 조회한다.
	 * 
	 * @param xmlStr
	 * @param xpath
	 * @param prefixSplit
	 *            : 접두어 삭제 유무
	 * @return
	 * @throws Exception
	 */
	public static List<HashMap<String, String>> getListHashMapFromXml(String xmlStr, String xpath, boolean prefixSplit)
			throws Exception {
		Document doc = convertTextToXmlDoc(xmlStr);
		return getListHashMapFromXml(doc, xpath, prefixSplit);
	}

	/**
	 * document 로부터 key = value 값을 조회한다.
	 * 
	 * @param doc
	 * @param xpath
	 * @return
	 * @throws Exception
	 */
	public static List<HashMap<String, String>> getListHashMapFromXml(Document doc, String xpath) throws Exception {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Node root = doc.getFirstChild();
		CachedXPathAPI cachedXPathAPI = new CachedXPathAPI();
		NodeIterator ni = cachedXPathAPI.selectNodeIterator(root, xpath);
		Node node = null;
		while ((node = ni.nextNode()) != null) {
			list.add(getHashMapFromXmlNode(node, false));
		}
		return list;
	}

	/**
	 * document 로부터 key = value 값을 조회한다.
	 * 
	 * @param doc
	 * @param xpath
	 * @param prefixSplit
	 *            : 접두어 삭제 유무
	 * @return
	 * @throws Exception
	 */
	public static List<HashMap<String, String>> getListHashMapFromXml(Document doc, String xpath, boolean prefixSplit)
			throws Exception {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Node root = doc.getFirstChild();
		CachedXPathAPI cachedXPathAPI = new CachedXPathAPI();
		NodeIterator ni = cachedXPathAPI.selectNodeIterator(root, xpath);
		Node node = null;
		while ((node = ni.nextNode()) != null) {
			list.add(getHashMapFromXmlNode(node, prefixSplit));
		}
		return list;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	/*
	 * private static HashMap<String, String> getHashMapFromXml(Node node) {
	 * HashMap<String, String> map = new HashMap<String, String>(); Node n;
	 * NodeList nList = node.getChildNodes();
	 * 
	 * for (int i = 0; i < nList.getLength(); i++ ) { n = nList.item(i);
	 * 
	 * if (n.getNodeType() == Node.ELEMENT_NODE) { map.put(n.getNodeName(),
	 * n.getTextContent()); } } return map; }
	 */

	/**
	 * 
	 * @param node
	 * @return
	 */
	private static HashMap<String, String> getHashMapFromXmlNode(Node node, boolean prefixSplit) {
		HashMap<String, String> map = new HashMap<String, String>();
		NodeList nList = node.getChildNodes();
		int p = 0;

		for (int i = 0; i < nList.getLength(); ++i) {
			Node n = nList.item(i);
			if (n.getNodeType() == 1) {
				if ((prefixSplit) && ((p = n.getNodeName().indexOf(":")) != -1))
					map.put(n.getLocalName(), n.getTextContent());
				else {
					map.put(n.getNodeName(), n.getTextContent());
				}
			}
		}
		return map;
	}

	/**
	 * XPath text() 일때 XML String 의 단일 값 리턴 - 하위 레벨의 데이터 태그가 존재하면 안됨
	 * 
	 * @param xmlStr
	 *            XML String
	 * @param xpath
	 *            조회할 XPath 문법
	 * @return 조회한 단일값
	 * @throws Exception
	 */
	public static String getTagValueFromXpath(String xmlStr, String xpath) throws Exception {
		Document doc = convertTextToXmlDoc(xmlStr);
		return getTagValueFromXpath(doc, xpath);
	}

	/**
	 * XPath text() 일때 Document 의 단일 값 리턴
	 * 
	 * @param doc
	 *            XML Document
	 * @param xpath
	 *            조회할 XPath 문법
	 * @return 조회한 단일값
	 * @throws Exception
	 */
	public static String getTagValueFromXpath(Document doc, String xpath) throws Exception {
		Node root = doc.getFirstChild();
		CachedXPathAPI cachedXPathAPI = new CachedXPathAPI();
		Node node = cachedXPathAPI.selectSingleNode(root, xpath);
		return (node != null ? node.getNodeValue() : null);
	}

	public static String documentToXml(Document document, String encoding) throws Exception {
		return elementToXml(document.getDocumentElement(), encoding);
	}

	public static String elementToXml(Element element, String encoding) throws Exception {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		OutputFormat outputformat = new OutputFormat();
		outputformat.setIndent(4);
		outputformat.setIndenting(true);
		outputformat.setPreserveSpace(false);

		XMLSerializer serializer = new XMLSerializer();
		serializer.setOutputFormat(outputformat);
		serializer.setOutputByteStream(stream);
		serializer.asDOMSerializer();
		serializer.serialize(element);

		StringBuilder stringBuilder = new StringBuilder(stream.toString(encoding));

		return stringBuilder.toString();
	}

	public static String nodeToXml(Node node) throws TransformerException {

		StringWriter sw = new StringWriter();

		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		t.transform(new DOMSource(node), new StreamResult(sw));

		return sw.toString();
	}
}
