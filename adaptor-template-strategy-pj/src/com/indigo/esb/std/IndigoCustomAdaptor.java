package com.indigo.esb.std;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_ID;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_NORMAL;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_POLL_TYPE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_REMOTE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_REPLY_QEUEE_NAME;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_RESEND;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_RESEND_TX_ID;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_FAIL;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_SUCCESS;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StopWatch;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.OnMessageStrategy;
import com.indigo.esb.adaptor.strategy.OnSignalStrategy;
import com.indigo.esb.config.InterfaceDataType;
import com.indigo.esb.config.InterfaceInfo;
import com.indigo.esb.signal.SignalListener;
import com.indigo.esb.util.JMSMessageUtil;
import com.indigo.indigomq.command.IndigoMQObjectMessage;

/**
 * 인터페이스로 분리함.
 * 
 * @author yoonjonghoon
 */
public class IndigoCustomAdaptor implements SignalListener, MessageListener, InitializingBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	protected JmsTemplate jmsTemplate;

	protected String adaptorName;
	protected Map<String, InterfaceInfo> interfaceMap;

	protected List<InterfaceInfo> interfaceList;

	protected List<OnSignalStrategy> OnSignalList;
	protected List<OnMessageStrategy> OnMessageList;

	protected Map<String, List<OnSignalStrategy>> onSignalPatternMap;
	protected Map<String, List<OnMessageStrategy>> onMessagePatternMap;

	String direction;

	String msgFileBackupPath = null;

	public void setMsgFileBackupPath(String msgFileBackupPath) {
		this.msgFileBackupPath = msgFileBackupPath;
	}

	public Map<String, InterfaceInfo> getInterfaceMap() {
		return interfaceMap;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * Schedule을 통한 호출 Method
	 */
	@Override
	public void onSignal(String if_id) {

		InterfaceInfo interfaceInfo = interfaceMap.get(if_id);
		do {
			if (!onSignal(interfaceInfo, null, ESB_NORMAL, null))
				break;
		} while (interfaceInfo.isOnSignalLoop());
	}
	
	/**
	 * loop 를 위한 OnSignal Method
	 *
	 * @param interfaceInfo
	 * @param esbResend
	 * @param map
	 * @return
	 */
	@SuppressWarnings("finally")
	public boolean onSignal(InterfaceInfo interfaceInfo, Message message, String esbPollType, Object attchObject) {

		StopWatch sw = new StopWatch(interfaceInfo.getInterfaceId());
		logger.info("Schedule Start :" + interfaceInfo.getInterfaceId());

		IndigoSignalResult onSignalResult = new IndigoSignalResult(interfaceInfo);
		if (attchObject != null) {
			onSignalResult.setAttchObject(attchObject);
		}

		if (message != null) {
			try {

				String resendTxid = JMSMessageUtil.getStringProperty(message, ESB_RESEND_TX_ID);
				putPropertie(message, onSignalResult);
				if (resendTxid != null) {
					onSignalResult.addProperty(ESB_TX_ID, resendTxid);
				}
			} catch (JMSException e) {
				logger.warn("onSignalResult Put Propertie : ", e);
			}
		}

		onSignalResult.addProperty(ESB_POLL_TYPE, esbPollType);

		boolean loop = true;
		String startClassName = "";

		if (interfaceInfo.getOnSignalPatternName() != null)
			OnSignalList = onSignalPatternMap.get(interfaceInfo.getOnSignalPatternName());

		onSignalResult.addProperty(ESB_TRANS_STATUS, ESB_TRANS_SUCCESS);

		try {
			for (OnSignalStrategy onSignal : OnSignalList) {
				try {
					startClassName = onSignal.toString();
					logger.info("## onStart ClassName : " + startClassName);
					sw.start(startClassName);
					onSignal.onStart(onSignalResult);
					sw.stop();
					if (onSignalResult.getProperty(ESB_SEND_ROW_COUNT).equals("0")) {
						loop = false;
						break;
					}
				} catch (Throwable e) {
					loop = false;
					sw.stop();
					logger.error("ERROR CLASS NAME : " + startClassName + " ERROR CONTENT : " + e, e);
					StringWriter swriter = new StringWriter();
					PrintWriter pw = new PrintWriter(swriter);
					e.printStackTrace(pw);
					logger.error("onsignal Error : ", e);

					onSignalResult.addProperty(ESB_TRANS_STATUS, ESB_TRANS_FAIL);
					onSignalResult.addProperty(ESB_ERR_MSG, swriter.toString());

					if (interfaceInfo.getAddDataMap().get(ESB_REPLY_QEUEE_NAME) != null) {
						onSignalResult.addProperty("tranid", interfaceInfo.getAddDataMap().get("tranid"));
						this.jmsTemplate.convertAndSend(interfaceInfo.getAddDataMap().get(ESB_REPLY_QEUEE_NAME),
								onSignalResult.getTemplateMessage(), onSignalResult.getProperties());
					}
				}
			}
		} finally {
			logger.info(interfaceInfo.getInterfaceId() + " OnSignal Commpite :" + "[" + onSignalResult.getProperty(ESB_SEND_ROW_COUNT)
					+ "] Row " + "\n " + sw.toString() + "\n Message Propreties : " + onSignalResult.getProperties().getHeaderInfoMap());
			return loop;
		}
	}

	public List<OnSignalStrategy> getOnSignalList() {
		return OnSignalList;
	}

	public void setOnSignalList(List<OnSignalStrategy> onSignalList) {
		OnSignalList = onSignalList;
	}

	public List<OnMessageStrategy> getOnMessageList() {
		return OnMessageList;
	}

	public void setOnMessageList(List<OnMessageStrategy> onMessageList) {
		OnMessageList = onMessageList;
	}

	/**
	 * Message 수신 시 호출 되는 Method
	 */
	@Override
	public void onMessage(Message message) {
		
		String interfaceId = JMSMessageUtil.getStringProperty(message, ESB_IF_ID);
		InterfaceInfo interfaceInfo = interfaceMap.get(interfaceId);
		String dataType = JMSMessageUtil.getStringProperty(message, ESB_IF_DATA_TYPE);

		switch (InterfaceDataType.valueOf(dataType)) {
		/**
		 * 수신 데이터 처리 이벤트
		 */
		case DATA:
			logger.debug("DATA Processing Event : " + interfaceInfo.getInterfaceId());
			processMessage(interfaceInfo, message, onMessagePatternMap.get(interfaceInfo.getOnMessagePatternName()));
			break;

		/**
		 * 결과 처리 이벤트
		 */
		case RESULT:
			logger.debug("Result Event : " + interfaceInfo.getInterfaceId());
			processResult(interfaceInfo, message);
			break;

		case REMOTE:
			try {
				logger.info(dataType + " Event : " + interfaceInfo.getInterfaceId());
				if (direction.equals("source")) {
					onSignal(interfaceInfo, message, ESB_REMOTE, null);
				} else if (direction.equals("target")) {
					processMessage(interfaceInfo, message, onMessagePatternMap.get(interfaceInfo.getOnMessagePatternName()));
				}
			} catch (Exception e) {
				logger.error("Remote Call JMS Property Init Error :  ", e);
			}
			break;

		case NORMAL:
			try {
				logger.info(dataType + " Event : " + interfaceInfo.getInterfaceId());
				if (direction.equals("source")) {
					onSignal(interfaceInfo, message, ESB_NORMAL, null);
				} else if (direction.equals("target")) {
					if (msgFileBackupPath != null) {
						File dir = new File(msgFileBackupPath);
						try {
							
							FileUtils.forceMkdir(new File(dir.getParent()));
							String fileName = msgFileBackupPath + File.separator + interfaceId + "_"
									+ JMSMessageUtil.getStringProperty(message, ESB_TX_ID);
							File backupFile = new File(fileName);
							
							if (backupFile.exists()) {
								backupFile.delete();
								backupFile.createNewFile();
							}
							
							RMessage rm = new RMessage();
							rm.setHeader( ((IndigoMQObjectMessage)message).getProperties());
							rm.setData( ((IndigoMQObjectMessage)message).getObject());
							
							FileUtils.writeByteArrayToFile(backupFile, SerializationUtils.serialize(rm));
							logger.info("File Backup Complete {} ", backupFile.getCanonicalPath());
							
						} catch (IOException e) {
							logger.error("Backup Fail : ", e);
						}
					}
					processMessage(interfaceInfo, message, onMessagePatternMap.get(interfaceInfo.getOnMessagePatternName()));
				}
			} catch (Exception e) {
				logger.error("Remote Call JMS Property Init Error :  ", e);
			}
			break;

		case RESEND:
			try {
				logger.info(dataType + " Event : " + interfaceInfo.getInterfaceId());
				if (direction.equals("source")) {
					onSignal(interfaceInfo, message, ESB_RESEND, null);
				} else if (direction.equals("target")) {
					processMessage(interfaceInfo, message, onMessagePatternMap.get(interfaceInfo.getOnMessagePatternName()));
				}
			} catch (Exception e) {
				logger.error("ReSend Call JMS Property Init Error :  ", e);
			}
			break;

		default:
			logger.warn("Invalid Data Process Type :  :  " + dataType);
		}
	}

	/**
	 * Data 수신시 호출 되는 Method
	 *
	 * @param interfaceInfo
	 * @param message
	 */
	private void processMessage(InterfaceInfo interfaceInfo, Message message, List<OnMessageStrategy> OnMessageList) {
		String interfaceId = interfaceInfo.getInterfaceId();

		String dataType = JMSMessageUtil.getStringProperty(message, ESB_IF_DATA_TYPE);
		StopWatch sw = new StopWatch(interfaceId + " " + dataType);
		IndigoMessageResult indigoMessageResult = new IndigoMessageResult(interfaceInfo, jmsTemplate, message);

		indigoMessageResult.setPocessStatus(ESB_TRANS_SUCCESS);

		for (OnMessageStrategy onMessage : OnMessageList) {

			try {
				sw.start(onMessage.toString());
				onMessage.process(indigoMessageResult);
				sw.stop();
			} catch (Throwable e) {
				sw.stop();
				StringWriter swriter = new StringWriter();
				PrintWriter pw = new PrintWriter(swriter);
				e.printStackTrace(pw);
				indigoMessageResult.setPocessStatus(ESB_TRANS_FAIL);

				indigoMessageResult.addProperty(ESB_ERR_MSG, swriter.toString());
				logger.error("processMessage error : ", e);
			}
		}

		logger.info(dataType + " Receive Process Complite :" + interfaceId + "\n" + sw.toString());
		
	}

	/**
	 * 결과 수신 시 호출 되는 Method
	 *
	 * @param interfaceInfo
	 * @param message
	 */
	private void processResult(InterfaceInfo interfaceInfo, Message message) {
		String interfaceId = interfaceInfo.getInterfaceId();
		String dataType = JMSMessageUtil.getStringProperty(message, ESB_IF_DATA_TYPE);
		StopWatch sw = new StopWatch(interfaceId + " " + dataType);
		IndigoMessageResult indigoMessageResult = new IndigoMessageResult(interfaceInfo, jmsTemplate, message);

		if (interfaceInfo.getOnMessagePatternName() != null && !interfaceInfo.getOnMessagePatternName().trim().equals(""))
			OnMessageList = onMessagePatternMap.get(interfaceInfo.getOnMessagePatternName());

		for (OnMessageStrategy onMessage : OnMessageList) {

			try {
				sw.start(onMessage.toString());
				onMessage.process(indigoMessageResult);
				sw.stop();
			} catch (Throwable e) {
				sw.stop();
				StringWriter swriter = new StringWriter();
				PrintWriter pw = new PrintWriter(swriter);
				e.printStackTrace(pw);
				indigoMessageResult.setPocessStatus(ESB_TRANS_FAIL);

				indigoMessageResult.addProperty(ESB_ERR_MSG, swriter.toString());
				logger.error("processResult error : ", e);
			}
		}

		logger.info(dataType + " Result Process Complite:" + interfaceId + " " + sw.toString());

	}

	private void putPropertie(Message message, IndigoSignalResult onSignalResult) throws JMSException {
		@SuppressWarnings("unchecked")
		Enumeration<Object> propertyKey = message.getPropertyNames();
		while (propertyKey.hasMoreElements()) {
			String key = propertyKey.nextElement().toString();
			onSignalResult.addProperty(key, message.getStringProperty(key));
		}

		if (message instanceof ObjectMessage) {
			onSignalResult.setPollResultDataObj(((ObjectMessage) message).getObject());
		} else if (message instanceof TextMessage) {
			onSignalResult.setPollResultDataObj(((TextMessage) message).getText());
		}
	}

	public void setInterfaceList(List<InterfaceInfo> interfaceList) {
		this.interfaceList = interfaceList;
	}

	public List<InterfaceInfo> getInterfaceList() {
		return interfaceList;
	}

	public String getAdaptorName() {
		return adaptorName;
	}

	public void setAdaptorName(String adaptorName) {
		this.adaptorName = adaptorName;
	}

	public Map<String, List<OnSignalStrategy>> getOnSignalPatternMap() {
		return onSignalPatternMap;
	}

	public void setOnSignalPatternMap(Map<String, List<OnSignalStrategy>> onSignalPatternMap) {
		this.onSignalPatternMap = onSignalPatternMap;
	}

	public Map<String, List<OnMessageStrategy>> getOnMessagePatternMap() {
		return onMessagePatternMap;
	}

	public void setOnMessagePatternMap(Map<String, List<OnMessageStrategy>> onMessagePatternMap) {
		this.onMessagePatternMap = onMessagePatternMap;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(interfaceList, "Property 'interfaceList' is Required");
		logger.info("Interface List  : " + getInterfaceList().size());
		interfaceMap = new HashMap<String, InterfaceInfo>();
		for (InterfaceInfo info : getInterfaceList()) {
			interfaceMap.put(info.getInterfaceId(), info);
			logger.info("Interface Information :" + info.toString());
		}
	}

}
