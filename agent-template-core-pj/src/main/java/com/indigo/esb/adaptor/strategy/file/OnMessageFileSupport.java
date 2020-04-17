package com.indigo.esb.adaptor.strategy.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.strategy.OnMessageStrategy;
import com.indigo.fileserver.client.IndigoFileTransferAPI;
import com.indigo.fileserver.event.IFileEvent;

public abstract class OnMessageFileSupport implements OnMessageStrategy, InitializingBean {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired(required = false)
	protected JmsTemplate jmsTemplate;
 
	public boolean socketReceive(String filename, String agentDir, String serverInfo) throws Exception {
		try {

			IFileEvent event = null;
			String serverTemp[] = serverInfo.split(":");
			String serverIp = serverTemp[0];
			int serverPort = Integer.parseInt(serverTemp[1]);

			IndigoFileTransferAPI api = new IndigoFileTransferAPI(serverIp, serverPort);
			api.setRepository(agentDir);
			String file = filename;
			api.setChunkSize(8192);
			api.setEvent(event);
			api.simpleFileGet("", file); //true:파일서버파일삭제
			log.info("File Socket Receive Success !!!");
			return true;
		} catch (Exception e) {
			log.error("File Socket Receive Fail !!!");
			//throw new RuntimeException("File Receive Fail  !!");
			return false;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

}
