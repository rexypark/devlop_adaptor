package com.indigo.esb.std.p02;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageDBSupport;

public class MessageFileToDBAfterProcess extends OnMessageDBSupport {

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		this.jmsTemplate.convertAndSend(indigoMessageResult.getInterfaceInfo().getTargetDestinationName(),
				indigoMessageResult.getTemplateMessage(""), indigoMessageResult.getProperties());
	}

}
