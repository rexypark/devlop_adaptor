package com.indigo.esb.std.db.relational;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalDBSupport;

public class P02OnSignalDBSendImpl extends OnSignalDBSupport {

	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		String sendDestinationName = onSignalResult.getInterfaceInfo().getTargetDestinationName();
		this.jmsTemplate.convertAndSend(sendDestinationName, onSignalResult.getTemplateMessage(),
				onSignalResult.getProperties());
	}
}
