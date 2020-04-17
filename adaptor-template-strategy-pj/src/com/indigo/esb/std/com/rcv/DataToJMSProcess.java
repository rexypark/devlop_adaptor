package com.indigo.esb.std.com.rcv;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_FAIL;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.OnMessageStrategy;
import com.indigo.esb.config.InterfaceInfo;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * @author clupine
 */
public class DataToJMSProcess implements OnMessageStrategy{

	@Autowired(required = false)
	protected JmsTemplate jmsTemplate;
	
	
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		
		this.jmsTemplate.convertAndSend(indigoMessageResult.getInterfaceInfo().getTargetDestinationName(),
				indigoMessageResult.getTemplateMessage(indigoMessageResult.getDataObj()), indigoMessageResult.getProperties());
	}

}
