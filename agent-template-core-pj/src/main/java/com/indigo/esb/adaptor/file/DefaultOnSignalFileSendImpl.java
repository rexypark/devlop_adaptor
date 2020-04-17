package com.indigo.esb.adaptor.file;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.file.OnSignalFileSupport;

public class DefaultOnSignalFileSendImpl extends OnSignalFileSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		// TODO Auto-generated method stub
		String sendDestinationName = onSignalResult.getInterfaceInfo().getTargetDestinationName();
		this.jmsTemplate.convertAndSend(sendDestinationName, onSignalResult.getTemplateMessage(),
				onSignalResult.getProperties());

	}

}
