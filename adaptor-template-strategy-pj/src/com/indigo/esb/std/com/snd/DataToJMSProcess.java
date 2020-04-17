package com.indigo.esb.std.com.snd;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;


/**
 * 사용 
 * Polling된 데이터 JMS 송신
 * @author clupine
 *
 */
public class DataToJMSProcess extends OnSignalSpacenameDBSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		String sendDestinationName = onSignalResult.getInterfaceInfo().getTargetDestinationName();
		this.jmsTemplate.convertAndSend(sendDestinationName, onSignalResult.getTemplateMessage(),
				onSignalResult.getProperties());
	}
	
	
}
