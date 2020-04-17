package com.indigo.esb.std.file.snd;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.file.OnSignalFileSupport;

/**
 * ���Ͽ����� meta������ ť�� �۽��Ѵ�
 * @author Administrator
 *
 */
public class DefaultOnSignalFileSendImpl extends OnSignalFileSupport {

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		// TODO Auto-generated method stub
		String sendDestinationName = onSignalResult.getInterfaceInfo().getTargetDestinationName();
		this.jmsTemplate.convertAndSend(sendDestinationName, onSignalResult.getTemplateMessage(),
				onSignalResult.getProperties());

	}

}
