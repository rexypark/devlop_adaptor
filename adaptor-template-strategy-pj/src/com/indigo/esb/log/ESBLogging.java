package com.indigo.esb.log;
import java.util.Date;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import com.indigo.esb.log.jms.ImsMsgHubAllData;
import com.indigo.esb.log.jms.ImsMsgHubEndData;
import com.indigo.esb.log.jms.ImsMsgHubStartData;

public class ESBLogging {

	private static final Logger logger = LoggerFactory.getLogger(ESBLogging.class);
	
	/**
	 * 실시간 처리에 대한 로그와 같이 시작/완료 로그를 ESB.LOGGING 큐로 전송합니다. logType은 ALL
	 * @param jmsTemplate
	 * @param txId
	 * @param trnnId
	 * @param count
	 * @param sndCode
	 * @param rcvCode
	 * @param msgCredt
	 * @param sndDt
	 * @throws Exception
	 */
	public static void oneLogging(JmsTemplate jmsTemplate ,String ifid, String txId , int count , int errCount , String sndCode , String rcvCode , String msgCredt ,String sndDt , String rcvDt , String resultMsg , String resultCode) throws Exception {
		ImsMsgHubAllData data = new ImsMsgHubAllData();
		data.setTxid(txId);
		data.setCount(count);
		data.setErrCount(errCount);
		data.setIfid(ifid);
		data.setSndCd(sndCode);
		data.setRcvCd(rcvCode);
		data.setResultCd(resultCode);
		data.setMsgCredt(msgCredt);
		data.setSndDt(sndDt);
		data.setRcvDt(rcvDt);
		data.setResultMsg(resultMsg);

		jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		jmsTemplate.convertAndSend("ESB.LOGGING", data, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message msg) throws JMSException {
				msg.setStringProperty("logType", "ALL");
				return msg;
			}
		});
	}


	/**
	 * 이력 시작 로그를 ESB.LOGGING 큐로 전송합니다. logType은 DETAIL
	 * @param jmsTemplate
	 * @param ifid
	 * @param txId
	 * @param count
	 * @param sndCode
	 * @param rcvCode
	 * @param msgCredt
	 * @param sndDt
	 * @throws Exception
	 */
	public static void startLogging(JmsTemplate jmsTemplate , String ifid, String txId , int count, String sndCode , String rcvCode , String msgCredt ,String sndDt) throws Exception {

		ImsMsgHubStartData data = new ImsMsgHubStartData();

		data.setTxid(txId);
		data.setCount(count);
		data.setIfid(ifid);
		data.setSndCd(sndCode);
		data.setRcvCd(rcvCode);
		data.setMsgCredt(msgCredt);
		data.setSndDt(sndDt);

		jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		jmsTemplate.convertAndSend("ESB.LOGGING", data, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message msg) throws JMSException {
				msg.setStringProperty("logType", "DETAIL");
				return msg;
			}
		});
	}

	/**
	 * 이력 완료 로그를 ESB.LOGGING 큐로 전송합니다. logType은 RESULT
	 * @param jmsTemplate
	 * @param ifid
	 * @param txId
	 * @param errCount
	 * @param resultCode
	 * @param resultMsg
	 * @param rcvDt
	 * @throws Exception
	 */
	public static void endLogging(JmsTemplate jmsTemplate , String ifid, String txId , int errCount , String resultCode , String resultMsg , String rcvDt) throws Exception {

		ImsMsgHubEndData data = new ImsMsgHubEndData();

		data.setTxid(txId);
		data.setErrCount(errCount);
		data.setIfid(ifid);
		data.setResultCd(resultCode);
		data.setResultMsg(resultMsg);
		data.setRcvDt(rcvDt);

		jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		jmsTemplate.convertAndSend("ESB.LOGGING", data, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message msg) throws JMSException {
				msg.setStringProperty("logType", "RESULT");
				return msg;
			}
		});
	}

	public static String parseDateString(java.util.Date date, String format) {
		java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat(format, java.util.Locale.KOREA);
		java.util.Calendar calendar = new java.util.GregorianCalendar();
		calendar.setTime(date);
		return fmt.format(calendar.getTime());
	}
	
	public static String now() {
		return parseDateString(new Date(), "yyyyMMddHHmmssSSS");
	}

}
