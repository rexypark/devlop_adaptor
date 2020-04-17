package com.indigo.esb.adaptor.db;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalDBSupport;

public class DefaultOnSignalDBSendImpl extends OnSignalDBSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		String sendDestinationName = onSignalResult.getInterfaceInfo().getTargetDestinationName();
		System.out.println("onSignalResult.getProperties :" + onSignalResult.getProperties().getHeaderInfoMap());
		this.jmsTemplate.convertAndSend(sendDestinationName, onSignalResult.getTemplateMessage(),
				onSignalResult.getProperties());
	}
}
