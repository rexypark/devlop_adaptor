package com.indigo.esb.adaptor.file;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.io.File;
import java.util.ArrayList;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.file.OnMessageFileSupport;
import com.indigo.esb.config.FileInterfaceInfo;

public class DefaultOnMessageFileAfterProcessImpl extends OnMessageFileSupport {

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		FileInterfaceInfo fileInfo = (FileInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		String sendDestinationName = fileInfo.getTargetDestinationName();
		this.jmsTemplate.convertAndSend(sendDestinationName, indigoMessageResult.getTemplateMessage(new ArrayList()),
				indigoMessageResult.getProperties());

		if (new File(fileInfo.getTmpDir(), indigoMessageResult.getProperty(ESB_TX_ID) + ".zip").exists())
			new File(fileInfo.getTmpDir(), indigoMessageResult.getProperty(ESB_TX_ID) + ".zip").delete();

	}
}
