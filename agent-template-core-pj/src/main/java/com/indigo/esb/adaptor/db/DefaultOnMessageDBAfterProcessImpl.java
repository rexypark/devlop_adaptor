package com.indigo.esb.adaptor.db;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.OnMessageStrategy;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.util.CollectionUtil;

public class DefaultOnMessageDBAfterProcessImpl implements OnMessageStrategy {
		@Autowired(required = false)
	protected JmsTemplate jmsTemplate;

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> recvList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		List<Map<String, Object>> resultList = CollectionUtil.makeFilterdMapList(dbInterfaceInfo.getReturnColNames(), recvList);

		this.jmsTemplate.convertAndSend(indigoMessageResult.getInterfaceInfo().getTargetDestinationName(),
				indigoMessageResult.getTemplateMessage(resultList), indigoMessageResult.getProperties());
	}

}
