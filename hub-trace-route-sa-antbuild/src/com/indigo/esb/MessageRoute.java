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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.jms.IndigoTemplateMessage;
import com.indigo.esb.route.DBRouteBean;
import com.indigo.esb.util.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.util.IndigoHeaderMessagePostProcessor;
import com.indigo.esb.util.XPathUtils;
import com.indigo.esb.util.XmlUtils;

public class MessageRoute implements MessageListener {

	private JmsTemplate jmsTemplate;
	private SqlSession sqlSession;

	private DBRouteBean routeData;
	protected transient Logger logger = LoggerFactory.getLogger(MessageRoute.class);
//	private String dlqName = "DLQ.SQU.IN.OTO";
	private boolean insertRowLog = false;
	private boolean insertTpLog = false;
	
	/**
	 * log trace key
	 * 
	 * 
	 * public static final String KEY_HEADER_COUNT = "count"; public static
	 * final String KEY_HEADER_INITIAL_TIME = "init_time"; public static final
	 * String KEY_HEADER_SEND_SERVER_CODE = "snd_srv_cd"; public static final
	 * String KEY_HEADER_RECEIVE_SERVER_CODE = "rcv_srv_cd"; public static final
	 * String KEY_HEADER_INTERFACE_ID = "if_id"; public static final String
	 * KEY_HEADER_TRANSACTION_ID = "tx_id"; public static final String
	 * KEY_HEADER_DESTINATION_ID = "dest_id";
	 * 
	 * 
	 */
	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	public void onMessage(Message message) {

		logger.info("=========================SQU-OTO Bean SU Start=========================");
		String indata = null;
		HashMap<String, String> headerMap = null;
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		Object sendMessage = null;
		Object pojo = null;
		try {
			if (message instanceof TextMessage) {

				logger.info("IndigoMessage Header Used Process ...");
				indata = getMessage(message);
				headerMap = XPathUtils.getHashMapFromXml(indata, "/root/sndheader");
				logger.debug("[SQU-OTO] HeaderMap :" + headerMap);
				logger.debug("Received Message: " + indata);
				sendMessage = indata;

			} else {

				pojo = this.jmsTemplate.getMessageConverter().fromMessage(message);
				logger.info("Template Message ..");
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

			if ((headerMap != null) && (headerMap.size() > 0)) {
				String if_id = headerMap.get("if_id");

				List<Map<String, Object>> routeList = getRouteData().getRouteMap().get(if_id);

				if(routeList == null) {
					//doSendMessage(dlqName, propertyMap, sendMessage);
				    	String api_queue = headerMap.get("api_queue");
				    	if(api_queue != null){
				    	    propertyMap.put("log_stat", "F");
				    	    propertyMap.put("err_msg", "Routing Error");
				    	    doSendMessage(api_queue, propertyMap, sendMessage);
				    	}

				    	//버릴거면 오류 내고 끝내고
				    	if(getRouteData().isDiscardMessage()){
				    		throw new Exception("Route_Info 정보를 확인 하세요   " + if_id + " 에 해당하는 Dest_id가 없습니다.");
				    	}else{
				    	//안버릴거면 리다이렉트 큐로 전송
				    		doSendMessage(getRouteData().getFailRedirectQueue(), propertyMap, sendMessage);
				    		return;
				    	}
				}

				try {
					/**
					 * 로그 기록
					 * headerMap 프로퍼티 재전송 메시지 검사해서 재전송이면 업데이트를 진행
					 */
					if(propertyMap.containsKey(IndigoHeaderJMSPropertyConstants.ESB_POLL_TYPE)){
						String poolType = propertyMap.get(IndigoHeaderJMSPropertyConstants.ESB_POLL_TYPE);
						if(poolType.equals(IndigoHeaderJMSPropertyConstants.ESB_NORMAL) || poolType.equals(IndigoHeaderJMSPropertyConstants.ESB_REMOTE) ){
							insertMasterLog(headerMap, routeList.size()<=1);
						}else if(poolType.equals(IndigoHeaderJMSPropertyConstants.ESB_RESEND)){
							updateMasterLog(headerMap, routeList.size()<=1);
						}
					}else{
						insertMasterLog(headerMap, routeList.size()<=1);
					}
					if(insertTpLog && propertyMap.get(IndigoHeaderJMSPropertyConstants.ESB_IF_TYPE).equals("TP")){
					    insertTPlog(headerMap, pojo);
					}
				} catch (Exception ex) {
					logger.error("Message Master Insert Fail : " + ex);
				}
				
				
				/**
				 * 개별 재전송 구현 2014-10-17 clupine
				 */
				Set<String> resendSet = null;
				if(propertyMap.containsKey(IndigoHeaderJMSPropertyConstants.ESB_RESEND_DEST_ID)){
					String destIds = propertyMap.get(IndigoHeaderJMSPropertyConstants.ESB_RESEND_DEST_ID);
					String[] destsArr = destIds.split(",");
					resendSet = new HashSet<String>(Arrays.asList(destsArr));
				}
				
				for (Map<String, Object> routData : routeList) {
					
					if(resendSet != null && !resendSet.contains(routData.get("DEST_ID"))){
						continue;
					}
									
					routeProcess(headerMap, propertyMap, sendMessage, if_id,routData);
				}
				
				
			} else {
				throw new Exception("XML Header Null Error :/root/sndheader  Header doesn't exist in this message");
			}

		} catch (Exception ex) {
			if ((ex instanceof SQLException))
				getNextSqlException((SQLException) ex);
			logger.error("[SQU-OTO] :" + ex.getMessage(), ex);

			try {
				if (!(ex instanceof SQLException)) {
					List<HashMap<String, String>> errBodyList = new ArrayList<HashMap<String, String>>();
					setErrorData(errBodyList, ex, 0, null);
					headerMap.put("err_cnt", headerMap.get("count"));
					insertCVTResultLog(headerMap, XmlUtils.makeXmlMessage(headerMap, errBodyList));
				}
			} catch (Exception subEx) {
				if ((subEx instanceof SQLException))
					getNextSqlException((SQLException) subEx);
				logger.error(subEx.getMessage(), subEx);
			}
		}
		logger.info("=========================SQU-OTO Bean END=========================");
	}

	private void routeProcess(HashMap<String, String> headerMap, HashMap<String, String> propertyMap, Object sendMessage,
			String if_id, Map<String, Object> routData) {
		
		logger.info("#### routeData : " + routData);
		logger.info("[SQU-OTO] IF ID :" + if_id + ", Queue Name :"
				+ routData.get("DATA_DEST_QUEUE").toString() 
				+ ", Reply_Queue Name :" + routData.get("RESULT_DEST_QUEUE").toString() 
				+ ", Dest ID :" + routData.get("DEST_ID"));
		propertyMap.put("dest_id", routData.get("DEST_ID").toString());
		headerMap.put("dest_id", routData.get("DEST_ID").toString());
		headerMap.put(IndigoHeaderJMSPropertyConstants.ESB_SEND_SYSTEM_ID, routData.get("SND_CD").toString());
		headerMap.put(IndigoHeaderJMSPropertyConstants.ESB_RECEIVE_SYSEM_ID, routData.get("RCV_CD").toString());
		
		// result Queue Name setting
		if(headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_REPLY_QEUEE_NAME) == null){
				headerMap.put("reply_queue",
					routData.get("RESULT_DEST_QUEUE").toString());
				propertyMap.put("reply_queue",
					routData.get("RESULT_DEST_QUEUE").toString());
		}

		//개별 메시지 보내고
		doSendMessage(routData.get("DATA_DEST_QUEUE").toString(), propertyMap, sendMessage);
		
		try {
			//디테일에 기록
			if(propertyMap.containsKey(IndigoHeaderJMSPropertyConstants.ESB_POLL_TYPE)){
				String poolType = propertyMap.get(IndigoHeaderJMSPropertyConstants.ESB_POLL_TYPE);
				if(poolType.equals(IndigoHeaderJMSPropertyConstants.ESB_NORMAL) || poolType.equals(IndigoHeaderJMSPropertyConstants.ESB_REMOTE)){
					insertDetailLog(headerMap, routData.get("DEST_ID").toString());
				}else if(poolType.equals(IndigoHeaderJMSPropertyConstants.ESB_RESEND)){
					updateDetailLog(headerMap, routData.get("DEST_ID").toString());
				}
			}else{
				insertDetailLog(headerMap, routData.get("DEST_ID").toString());
			}
			if(insertRowLog){
			    insertRowLog(headerMap, sendMessage);
			}
		} catch (Exception e) {
			logger.error("Message Detail Insert Fail : " + e);
		}
		
	}

	public void doSendMessage(String qName, HashMap<String, String> propertyMap, Object message) {
		IndigoHeaderMessagePostProcessor postProcessor = new IndigoHeaderMessagePostProcessor();
		propertyMap.remove("TEMPLATE_DATA");
		postProcessor.setHeaderInfoMap(propertyMap);
		getJmsTemplate().convertAndSend(qName, message, postProcessor);
	}
	public void doSendMessage(String qName, Object message) {
		getJmsTemplate().convertAndSend(qName, message);
	}
	public void insertTPlog(HashMap<String, String> headerMap, Object pojo){
	    HashMap<String, Object> dataMap = new HashMap<String, Object>();
	    IndigoTemplateMessage message = (IndigoTemplateMessage) pojo;
	    @SuppressWarnings("unchecked")
		List<Map<String, Object>> dataList = (List<Map<String, Object>>)message.getDataObj();
	    Map<String, Object> map = null;
	    for ( Map<String, Object> val : dataList) {
		map = val;
	    }
	    byte[] tranData = (byte[])map.get("DATA");
	    Iterator<String> it = headerMap.keySet().iterator();
	    dataMap.put("LOG_TIME", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME).substring(0, 17));
	    dataMap.put("COMM_TYPE", "TCP");
	    if(headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE).equals("DATA")){
		dataMap.put("COMM_OP","W");
		dataMap.put("INOUT_FLAG","O");
	    }else{
		dataMap.put("COMM_OP","R");
		dataMap.put("INOUT_FLAG","I");
	    }
	    dataMap.put("TRAN_DATA",tranData);
	    dataMap.put("STATUS","S");
	    while (it.hasNext()) {
		String key = it.next();
		String value = headerMap.get(key);
		dataMap.put(key.toUpperCase(), value);
	    }
	    logger.debug("dataMap :" + dataMap);
	    getSqlSession().insert("TPLOG", dataMap);
	}
	public void insertMasterLog(HashMap<String, String> headerMap, boolean msgType) throws Exception {

		try {
			
			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			Iterator<String> it = headerMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = headerMap.get(key);
				dataMap.put(key.toUpperCase(), value);
			}

			dataMap.put("MSG_CREATE_DT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME));
			dataMap.put("HUB_RCV_DT", parseDateString(new Date(), "yyyyMMddHHmmssSSS"));
			dataMap.put("MSG_CNT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT));
			dataMap.put("DATA_TYPE", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE));
			if(headerMap.containsKey("FILE_NAME")){
			    dataMap.put("FILE_NAME", headerMap.get("FILE_NAME"));
			}

			if (msgType) {
				dataMap.put("MSG_TYPE", "N");
			} else {
				dataMap.put("MSG_TYPE", "T");
			}
			getSqlSession().insert("MASTER", dataMap);

		} catch (Exception e) {
			logger.error(
					"HubLogDB Master Table Insert Error : transaction id = "
							+ headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID) + " ;\n" + e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 재전송용
	 * @param headerMap
	 * @param msgType
	 * @throws Exception
	 */
	public void updateMasterLog(HashMap<String, String> headerMap, boolean msgType) throws Exception {
		
		try {
			
			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			Iterator<String> it = headerMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = headerMap.get(key);
				dataMap.put(key.toUpperCase(), value);
			}
			
			dataMap.put("MSG_CREATE_DT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME));
			dataMap.put("HUB_RCV_DT", parseDateString(new Date(), "yyyyMMddHHmmssSSS"));
			dataMap.put("MSG_CNT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT));
			dataMap.put("DATA_TYPE", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE));
			if(headerMap.containsKey("FILE_NAME")){
				dataMap.put("FILE_NAME", headerMap.get("FILE_NAME"));
			}
			
			if (msgType) {
				dataMap.put("MSG_TYPE", "N");
			} else {
				dataMap.put("MSG_TYPE", "T");
			}
			getSqlSession().update("MASTER_UPDATE", dataMap);
			
		} catch (Exception e) {
			logger.error(
					"HubLogDB Master Table update Error : transaction id = "
							+ headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID) + " ;\n" + e.getMessage(), e);
			throw e;
		}
	}

	public void insertDetailLog(HashMap<String, String> headerMap, String dest_id) throws Exception {

		try {

			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			Iterator<String> it = headerMap.keySet().iterator();
			
			while (it.hasNext()) {
				String key = it.next();
				String value = headerMap.get(key);
				dataMap.put(key.toUpperCase(), value);
			}

			dataMap.put("SND_CNT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT));
			dataMap.put("SND_CD", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_SEND_SYSTEM_ID));
			dataMap.put("RCV_CD", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_RECEIVE_SYSEM_ID));
			
			dataMap.put("DEST_ID", dest_id);
			dataMap.put("MSG_CNT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT));
			dataMap.put("MSG_CREATE_DT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME));
			dataMap.put("HUB_CVT_DT", parseDateString(new Date(), "yyyyMMddHHmmssSSS"));

			getSqlSession().insert("DETAIL", dataMap);

		} catch (Exception e) {
			logger.error(
					"HubLogDB Detail Table Insert Error : transaction id = "
							+ headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID) + " ;\n" + e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 재전송용
	 * @param headerMap
	 * @param dest_id
	 * @throws Exception
	 */
	public void updateDetailLog(HashMap<String, String> headerMap, String dest_id) throws Exception {
		
		try {
			
			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			Iterator<String> it = headerMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = headerMap.get(key);
				dataMap.put(key.toUpperCase(), value);
			}
			
			dataMap.put("SND_CNT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT));
			dataMap.put("SND_CD", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_SEND_SYSTEM_ID));
			dataMap.put("RCV_CD", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_RECEIVE_SYSEM_ID));
			
			dataMap.put("DEST_ID", dest_id);
			dataMap.put("MSG_CNT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT));
			dataMap.put("MSG_CREATE_DT", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_INIT_TIME));
			dataMap.put("HUB_CVT_DT", parseDateString(new Date(), "yyyyMMddHHmmssSSS"));
			
			getSqlSession().update("DETAIL_UPDATE", dataMap);
			
		} catch (Exception e) {
			logger.error(
					"HubLogDB Detail Table update Error : transaction id = "
							+ headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID) + " ;\n" + e.getMessage(), e);
			throw e;
		}
	}

	public void insertCVTResultLog(HashMap<String, String> headerMap, String xmlMsg) throws Exception {
		try {

			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			Iterator<String> it = headerMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = headerMap.get(key);
				dataMap.put(key.toUpperCase(), value);
			}
			dataMap.put("CVT_STATUS_CD", "F");
			dataMap.put("MSG_CVT_DT", DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS"));
			dataMap.put("ERR_CNT", headerMap.get("err_cnt"));
			dataMap.put("ERR_MSG", xmlMsg.getBytes("EUC-KR"));

			getSqlSession().insert("DETAIL", dataMap);

			logger.debug("SA Error Data have been recorded on IMS_MSG_HUB_CVT_RESULT_LOG.");
		} catch (Exception se) {
			throw se;
		}
	}
	public void insertRowLog(HashMap<String, String> headerMap, Object pojo) throws Exception {

		try {

			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			Iterator<String> it = headerMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = headerMap.get(key);
				dataMap.put(key.toUpperCase(), value);
			}
			
			dataMap.put("TX_ID", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID));
			dataMap.put("SND_CD", headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_SEND_SYSTEM_ID));
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>)pojo;
			for (Map<String, Object> map : list) {
			    String transcId = (String)map.get("TRANSC_ID");
			    dataMap.put("TRANSC_ID", transcId);
			    getSqlSession().insert("ROW", dataMap);
			}

		} catch (Exception e) {
			logger.error(
					"HubLogDB row Table Insert Error : transaction id = "
							+ headerMap.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID) + " ;\n" + e.getMessage(), e);
			throw e;
		}
	}
	public static void setErrorData(List<HashMap<String, String>> errDataList, Throwable e, int count,
			HashMap<String, String> dataMap) {
		if (dataMap == null) {
			dataMap = new HashMap<String, String>();
		}
		dataMap.put("err_data", e.getMessage());
		dataMap.put("err_seq", Integer.toString(count));
		errDataList.add(dataMap);
	}

	protected String getNextSqlException(SQLException sqlException) {
		SQLException sqlex = sqlException;
		StringBuffer errMsg = new StringBuffer();
		do {
			errMsg.append("** SQL ERR CODE : ");
			errMsg.append(sqlex.getErrorCode());
			errMsg.append("\t");
			errMsg.append("** SQL ERR MSG :");
			errMsg.append(sqlex.getMessage());
			errMsg.append("\n");
			sqlex = sqlex.getNextException();
		} while (sqlex != null);
		logger.error("[SQU-OTO] " + errMsg.toString(), sqlException);
		return errMsg.toString();
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

	public static String parseDateString(java.util.Date date, String format) {
		java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat(format, java.util.Locale.KOREA);
		java.util.Calendar calendar = new java.util.GregorianCalendar();
		calendar.setTime(date);
		return fmt.format(calendar.getTime());
	}

	public JmsTemplate getJmsTemplate() {
		return this.jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

//	public void setDlqName(String dlqName) {
//		this.dlqName = dlqName;
//	}

	public void setRouteData(DBRouteBean routeData) {
		this.routeData = routeData;
	}

	public DBRouteBean getRouteData() {
		return routeData;
	}

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	public SqlSession getSqlSession() {
		return sqlSession;
	}
	public void setInsertRowLog(boolean insertRowLog) {
	    this.insertRowLog = insertRowLog;
	}
	public void setInsertTpLog(boolean insertTpLog) {
	    this.insertTpLog = insertTpLog;
	}

}
