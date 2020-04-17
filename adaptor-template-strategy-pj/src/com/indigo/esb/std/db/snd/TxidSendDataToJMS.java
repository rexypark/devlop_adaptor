package com.indigo.esb.std.db.snd;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;


/**
 * 데이터 JMS 송신
 * @author clupine
 *
 */
public class TxidSendDataToJMS extends OnSignalSpacenameDBSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		String sendDestinationName = onSignalResult.getInterfaceInfo().getTargetDestinationName();
		
		this.jmsTemplate.convertAndSend(sendDestinationName, onSignalResult.getTemplateMessage(),
				onSignalResult.getProperties());
	}
}
