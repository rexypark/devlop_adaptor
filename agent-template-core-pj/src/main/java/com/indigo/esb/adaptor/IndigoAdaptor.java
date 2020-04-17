package com.indigo.esb.adaptor;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_ID;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_SEND_ROW_COUNT;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_FAIL;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.OnMessageStrategy;
import com.indigo.esb.adaptor.strategy.OnSignalStrategy;
import com.indigo.esb.config.InterfaceDataType;
import com.indigo.esb.config.InterfaceInfo;
import com.indigo.esb.signal.SignalListener;
import com.indigo.esb.util.JMSMessageUtil;

/**
 * 
 * 
 * @author yoonjonghoon
 * 
 */
public class IndigoAdaptor implements SignalListener, MessageListener, InitializingBean {

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

	@Override
	public void onSignal(String if_id) {
		InterfaceInfo interfaceInfo = interfaceMap.get(if_id);
		do {
			if (!onSignal(interfaceInfo))
				break;
		} while (interfaceInfo.isOnSignalLoop());
	}

	@SuppressWarnings("finally")
	public boolean onSignal(InterfaceInfo interfaceInfo) {

		StopWatch sw = new StopWatch(interfaceInfo.getInterfaceId());
		logger.info("스케쥴 시작:" + interfaceInfo.getInterfaceId());
		IndigoSignalResult onSignalResult = new IndigoSignalResult(interfaceInfo);
		boolean loop = true;
		String startClassName = "";
		try {

			if (interfaceInfo.getOnSignalPatternName() != null)
				OnSignalList = onSignalPatternMap.get(interfaceInfo.getOnSignalPatternName());

			for (OnSignalStrategy onSignal : OnSignalList) {
				startClassName = onSignal.toString();
				sw.start(startClassName);
				onSignal.onStart(onSignalResult);
				sw.stop();
				if (Integer.parseInt(onSignalResult.getProperty(ESB_SEND_ROW_COUNT)) == 0) {
					loop = false;
					break;
				}
			}

		} catch (Throwable e) {
			sw.stop();
			loop = false;
			logger.error("ERROR CLASS NAME : " + startClassName + " ERROR CONTENT : " + e);
			e.printStackTrace();
		} finally {
			logger.info(interfaceInfo.getInterfaceId() + " 스케쥴 완료:" + "["
					+ onSignalResult.getProperty(ESB_SEND_ROW_COUNT) + "]건" + "\n " + sw.toString()
					+ "\n Message Propreties : " + onSignalResult.getProperties().getHeaderInfoMap());
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

	@Override
	public void onMessage(Message message) {
		String interfaceId = JMSMessageUtil.getStringProperty(message, ESB_IF_ID);
		InterfaceInfo interfaceInfo = interfaceMap.get(interfaceId);
		String dataType = JMSMessageUtil.getStringProperty(message, ESB_IF_DATA_TYPE);
		logger.info(dataType + " 수신 처리 시작:" + interfaceId);

		switch (InterfaceDataType.valueOf(dataType)) {
		case DATA:
			processMessage(interfaceInfo, message);
			break;
		case RESULT:
			processResult(interfaceInfo, message);
			break;
		default:
			throw new IllegalArgumentException("수신한 Message의 JMSProperty'" + ESB_IF_DATA_TYPE
					+ "' property는 반드시 있어야합니다. [InterfaceDataType.DATA | InterfaceDataType.RESULT]");
		}
	}

	private void processMessage(InterfaceInfo interfaceInfo, Message message) {
		String interfaceId = interfaceInfo.getInterfaceId();
		String dataType = JMSMessageUtil.getStringProperty(message, ESB_IF_DATA_TYPE);
		StopWatch sw = new StopWatch(interfaceId + " " + dataType);
		IndigoMessageResult indigoMessageResult = new IndigoMessageResult(interfaceInfo, jmsTemplate, message);
		indigoMessageResult.setPocessStatus(ESB_TRANS_SUCCESS);

		if (interfaceInfo.getOnMessagePatternName() != null)
			OnMessageList = onMessagePatternMap.get(interfaceInfo.getOnMessagePatternName());

		for (OnMessageStrategy onMessage : OnMessageList) {

			try {
				sw.start(onMessage.toString());
				onMessage.process(indigoMessageResult);
				sw.stop();

			} catch (Throwable e) {
				sw.stop();
				indigoMessageResult.setPocessStatus(ESB_TRANS_FAIL);
				indigoMessageResult.addProperty(ESB_ERR_MSG, e.toString());
				e.printStackTrace();
				logger.error(e.toString());
			}
		}

		logger.info(dataType + " 수신 처리 완료:" + interfaceId + "\n" + sw.toString());
	}

	private void processResult(InterfaceInfo interfaceInfo, Message message) {
		String interfaceId = interfaceInfo.getInterfaceId();
		String dataType = JMSMessageUtil.getStringProperty(message, ESB_IF_DATA_TYPE);
		StopWatch sw = new StopWatch(interfaceId + " " + dataType);
		IndigoMessageResult indigoMessageResult = new IndigoMessageResult(interfaceInfo, jmsTemplate, message);

		try {
			if (interfaceInfo.getOnMessagePatternName() != null
					&& !interfaceInfo.getOnMessagePatternName().trim().equals(""))
				OnMessageList = onMessagePatternMap.get(interfaceInfo.getOnMessagePatternName());

			for (OnMessageStrategy onMessage : OnMessageList) {
				sw.start(onMessage.getClass().getName());
				onMessage.process(indigoMessageResult);
				sw.stop();
			}

		} catch (Exception e) {
			sw.stop();
			logger.error(e.toString());

		} finally {
			logger.info(dataType + " 결과 수신 처리 완료:" + interfaceId + " " + sw.toString());
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
		logger.info("인터페이스 목록 : " + getInterfaceList().size());
		for (InterfaceInfo info : getInterfaceList()) {
			interfaceMap = new HashMap<String, InterfaceInfo>();
			interfaceMap.put(info.getInterfaceId(), info);
			logger.info("인터페이스 정보 :" + info.toString());
		}
	}

}
