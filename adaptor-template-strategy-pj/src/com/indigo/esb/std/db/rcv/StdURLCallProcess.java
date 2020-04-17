package com.indigo.esb.std.db.rcv;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TRANS_SUCCESS;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.client.RestTemplate;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.OnMessageStrategy;
import com.indigo.esb.config.DBInterfaceInfoURLCall;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

public class StdURLCallProcess implements OnMessageStrategy, InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(StdURLCallProcess.class);

	RestTemplate restTemplate;

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		DBInterfaceInfoURLCall interfaceInfo = (DBInterfaceInfoURLCall) indigoMessageResult.getInterfaceInfo();

		if (ESB_TRANS_SUCCESS.equals(indigoMessageResult.getPocessStatus()) && interfaceInfo.isUseURLCall()) {
			String url = interfaceInfo.getTargetURL() + "/api/articles";
			log.info("호출대상 URL:{}", url);
			URI uri = URI.create(url);
			String request = indigoMessageResult.getProperty(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
			String response = restTemplate.postForObject(uri, request, String.class);
			log.info("URL 호출에 대한 응답: {}", response);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		restTemplate = new RestTemplate();
	}

}
