/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indigo.esb;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.jms.IndigoHeaderMessagePostProcessor;
import com.indigo.esb.jms.IndigoTemplateMessage;
import com.indigo.esb.route.DBRouteBean;
import com.indigo.esb.util.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.util.XPathUtils;

public class ReturnMessage implements MessageListener {

	private JmsTemplate jmsTemplate;
	private String dlqName = "DLQ.RQU.IN.OTO";
	private final String KEY_LOG_FLAG = "log";
	private final String KEY_LOG_FLAG_ERROR = "E";
	private final String XMLPath = "/root/sndheader";
	private DBRouteBean routeData;
	private SqlSession sqlSession;
	private boolean insertTpLog = false;
	protected static final Logger logger = LoggerFactory.getLogger(ReturnMessage.class);

	public void setRouteData(DBRouteBean routeData) {
		this.routeData = routeData;
	}

	public DBRouteBean getRouteData() {
		return routeData;
	}

	private String findDestQueueName(String ifId, String destId) {
		List<Map<String, Object>> routeList = (List<Map<String, Object>>) getRouteData().getRouteMap().get(ifId);

		for (Map<String, Object> routData : routeList) {
			if (routData.get("DEST_ID").equals(destId)) {
				return (String) routData.get("RESULT_DEST_QUEUE");
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see .MessageExchange)
	 */
	public void onMessage(Message message) {
		logger.info("=========================RQU-OTO Bean SU Start=========================");
		String indata = null;
		HashMap<String, String> headerMap = null;
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		String tx_id = null;
		String dest_id = null;
		String replyQueueName = null;
		Object sendMessage = null;

		try {

			if (message instanceof TextMessage) {

				logger.info("IndigoMessage Header Used Process ...");
				indata = getMessage(message);
				headerMap = XPathUtils.getHashMapFromXml(indata, "/root/sndheader");
				logger.debug("[SQU-OTO] HeaderMap :" + headerMap);
				logger.debug("Received Message: " + indata);
				sendMessage = indata;

			} else {

				Object pojo = this.jmsTemplate.getMessageConverter().fromMessage(message);
				logger.info("템플릿 메세지 ..");
				logger.info("Property Used Process ...");

				@SuppressWarnings("unchecked")
				Enumeration<Object> propertyKey = message.getPropertyNames();

				while (propertyKey.hasMoreElements()) {
					String key = propertyKey.nextElement().toString();
					propertyMap.put(key, message.getStringProperty(key));
				}

				sendMessage = pojo;
				headerMap = propertyMap;
			}

			logger.debug(this.getClass().getSimpleName() + " Message Header " + headerMap);
			String logStat = headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS);
			String ifId = headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_IF_ID);

			if ((headerMap != null) && (headerMap.size() > 0)) {
				tx_id = headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
				dest_id = headerMap.get("dest_id");
				// replyQueueName =
				// headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_REPLY_QEUEE_NAME);
				replyQueueName = findDestQueueName(ifId, dest_id);

				if ((tx_id == null) || (tx_id.trim().equals("")) || (logStat == null) || (logStat.trim().equals(""))) {
					logger.error("PropertiesMap :" + propertyMap);
					logger.error("HeaderMap :" + headerMap);
					doSendMessage(dlqName, propertyMap, sendMessage);
					throw new Exception(
							"Value Null or \"\" Error \n Message Properties  XML Header  \"dest_id\" or \"tx_id\" or \"log_state\" 값이 없습니다. ");
				}
				try {

					/**
					 * 로그 기록 headerMap 프로퍼티 재전송 결과 메시지 검사해서 재전송이면 업데이트를 진행 구현해야함
					 */
					if (propertyMap.containsKey(IndigoHeaderJMSPropertyConstants.ESB_POLL_TYPE)) {

						String poolType = propertyMap.get(IndigoHeaderJMSPropertyConstants.ESB_POLL_TYPE);
						if (poolType.equals(IndigoHeaderJMSPropertyConstants.ESB_NORMAL)
								|| poolType.equals(IndigoHeaderJMSPropertyConstants.ESB_REMOTE)) {
							insertRcvResultLog(headerMap, dest_id);
						} else if (poolType.equals(IndigoHeaderJMSPropertyConstants.ESB_RESEND)) {
							updateRcvResultLog(headerMap, dest_id);
						}
					} else {
						insertRcvResultLog(headerMap, dest_id);
					}

					if (insertTpLog && propertyMap.get(IndigoHeaderJMSPropertyConstants.ESB_IF_TYPE).equals("TP")) {
						insertTPlog(headerMap, sendMessage);
					}
				} catch (Exception ex) {
					logger.error("error : ", ex);
				}

				propertyMap.put(KEY_LOG_FLAG, "Y");
				logger.debug("[RQU-OTO] PropertiesMap :" + propertyMap);
				logger.debug("[RQU-OTO] HeaderMap :" + headerMap);
				logger.info("[RQU-OTO] Reply Queue Name :"
						+ (replyQueueName == null ? " # No Reply Queue Name # " : replyQueueName));

				if ((replyQueueName != null) && (!replyQueueName.trim().equals(""))) {
					doSendMessage(replyQueueName, propertyMap, sendMessage);
				} else {
					throw new Exception("Reuturn Queue Name is Null : " + replyQueueName);
				}
			} else {
				throw new Exception("XML Header Null Error :" + XMLPath + "  Header doesn't exist in this message");
			}
		} catch (Exception se) {
			logger.error("[RQU-OTO] :" + se.getMessage(), se);
		}
		logger.info("=========================RQU-OTO Bean END=========================");
	}

	public void doSendMessage(String qName, HashMap<String, String> propertyMap, Object message) {
		try {
			IndigoHeaderMessagePostProcessor postProcessor = new IndigoHeaderMessagePostProcessor();
			propertyMap.remove("TEMPLATE_DATA");
			postProcessor.setHeaderInfoMap(propertyMap);
			getJmsTemplate().convertAndSend(qName, message, postProcessor);
			logger.info("Send Message Success !! " + propertyMap);
		} catch (Exception e) {
			propertyMap.remove("TEMPLATE_DATA");
			logger.error("## Send Message Fail : " + e);
			logger.error("## Send Message Map !! : " + propertyMap);
		}
	}

	public void insertTPlog(HashMap<String, String> headerMap, Object pojo) {
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		IndigoTemplateMessage message = (IndigoTemplateMessage) pojo;
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) message.getDataObj();
		Map<String, Object> map = null;
		for (Map<String, Object> val : dataList) {
			map = val;
		}
		byte[] tranData = (byte[]) map.get("DATA");
		Iterator<String> it = headerMap.keySet().iterator();
		dataMap.put("LOG_TIME", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME).substring(0, 17));
		dataMap.put("COMM_TYPE", "TCP");
		if (headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE).equals("DATA")) {
			dataMap.put("COMM_OP", "W");
			dataMap.put("INOUT_FLAG", "O");
		} else {
			dataMap.put("COMM_OP", "R");
			dataMap.put("INOUT_FLAG", "I");
		}
		dataMap.put("TRAN_DATA", tranData);
		dataMap.put("STATUS", "S");
		while (it.hasNext()) {
			String key = it.next();
			String value = headerMap.get(key);
			dataMap.put(key.toUpperCase(), value);
		}
		logger.debug("dataMap :" + dataMap);
		getSqlSession().insert("TPLOG", dataMap);
	}

	public void insertRcvResultLog(HashMap<String, String> headerMap, String destId) throws Exception {
		try {

			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			Iterator<String> it = headerMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = headerMap.get(key);
				dataMap.put(key.toUpperCase(), value);
			}

			String logState = null;
			if (headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS) == null) {
				logState = "F";
			} else {
				logState = headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS);
			}

			if (logState.equals(KEY_LOG_FLAG_ERROR))
				logState = "F";

			dataMap.put("DEST_ID", destId);
			dataMap.put("STATUS_CD", logState);

			/**
			 * 메시지 수신시간을 new date()로 기록되어있어서 Target 수신 시간으로 변경함. 2014-09-04 clupine
			 */
			dataMap.put("MSG_RCV_DT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_RECEIVE_TIME));
			// dataMap.put("MSG_RCV_DT", parseDateString(new Date(), "yyyyMMddHHmmssSSS"));

			String errCnt = "0";
			if (headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_RECEIVE_ERR_ROW_COUNT) != null)
				errCnt = headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_RECEIVE_ERR_ROW_COUNT);
			dataMap.put("ERR_CNT", errCnt);

			if (logState.equals("S")) {
				dataMap.put("ERR_MSG",
						headerMap.toString().replace("{", "").replace("}", "").replace(", ", "\n").getBytes("EUC-KR"));
			} else {
				dataMap.put("ERR_MSG", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG).getBytes("EUC-KR"));
			}
			getSqlSession().insert("RESULT", dataMap);

		} catch (Exception se) {
			throw se;
		}
	}

	public void updateRcvResultLog(HashMap<String, String> headerMap, String destId) throws Exception {
		try {

			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			Iterator<String> it = headerMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = headerMap.get(key);
				dataMap.put(key.toUpperCase(), value);
			}

			String logState = null;
			if (headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS) == null) {
				logState = "F";
			} else {
				logState = headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS);
			}

			if (logState.equals(KEY_LOG_FLAG_ERROR))
				logState = "F";

			dataMap.put("DEST_ID", destId);

			if (logState.equals("S")) {
				dataMap.put("STATUS_CD", "R");
			} else {
				dataMap.put("STATUS_CD", logState);
			}

			/**
			 * 메시지 수신시간을 new date()로 기록되어있어서 Target 수신 시간으로 변경함. 2014-09-04 clupine
			 */
			dataMap.put("MSG_RCV_DT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_RECEIVE_TIME));
			// dataMap.put("MSG_RCV_DT", parseDateString(new Date(), "yyyyMMddHHmmssSSS"));

			String errCnt = "0";
			if (headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_RECEIVE_ERR_ROW_COUNT) != null)
				errCnt = headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_RECEIVE_ERR_ROW_COUNT);
			dataMap.put("ERR_CNT", errCnt);

			if (logState.equals("S")) {
				dataMap.put("ERR_MSG",
						headerMap.toString().replace("{", "").replace("}", "").replace(", ", "\n").getBytes("EUC-KR"));
			} else {
				dataMap.put("ERR_MSG", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG).getBytes("EUC-KR"));
			}
			getSqlSession().update("RESULT_UPDATE", dataMap);

		} catch (Exception se) {
			throw se;
		}
	}

	public static String parseDateString(java.util.Date date, String format) {
		java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat(format, java.util.Locale.KOREA);
		java.util.Calendar calendar = new java.util.GregorianCalendar();
		calendar.setTime(date);
		return fmt.format(calendar.getTime());
	}

	public static String getMessage(Message message) {
		String textMsg = null;
		if (message instanceof TextMessage)
			try {
				textMsg = ((TextMessage) message).getText();
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
		else {
			throw new RuntimeException("Message must be TestMessage type");
		}

		return textMsg;
	}

	public JmsTemplate getJmsTemplate() {
		return this.jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setDlqName(String dlqName) {
		this.dlqName = dlqName;
	}

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	public SqlSession getSqlSession() {
		return sqlSession;
	}

	public void setInsertTpLog(boolean insertTpLog) {
		this.insertTpLog = insertTpLog;
	}

}
