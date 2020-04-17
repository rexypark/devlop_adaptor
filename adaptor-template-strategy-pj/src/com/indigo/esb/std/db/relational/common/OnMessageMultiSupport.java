package com.indigo.esb.std.db.relational.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.strategy.OnMessageStrategy;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.fileserver.client.IndigoFileTransferAPI;
import com.indigo.fileserver.event.IFileEvent;

public abstract class OnMessageMultiSupport implements OnMessageStrategy,
		InitializingBean {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	protected JmsTemplate jmsTemplate;
	@Autowired(required = false)
	protected SqlSessionTemplate sqlSession;

	protected List<Map<String, Object>> makeResult(String colNamesStr,
			List<Map<String, Object>> recvList) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		String[] colNames = null;
		if (colNamesStr != null) {
			colNames = colNamesStr.split(",");
			Map<String, Object> resultMap = null;
			for (Map<String, Object> recordMap : recvList) {
				resultMap = new HashMap<String, Object>();
				for (String colName : colNames) {
					if (recordMap.containsKey(colName)) {
						resultMap.put(colName, recordMap.get(colName));
					}
				}
				resultList.add(resultMap);
			}
		}
		return resultList;

	}

	protected String getMybaitsSqlId(String interfaceId,
			QueryStatementType insert) {
		StringBuffer sb = new StringBuffer();
		sb.append(interfaceId);
		sb.append("_");
		sb.append(insert);
		return sb.toString();
	}

	public boolean socketReceive(String filename, String agentDir,
			String serverInfo){
		try {

			IFileEvent event = null;
			String serverTemp[] = serverInfo.split(":");
			String serverIp = serverTemp[0];
			int serverPort = Integer.parseInt(serverTemp[1]);

			IndigoFileTransferAPI api = new IndigoFileTransferAPI(serverIp,
					serverPort);
			api.setRepository(agentDir);
			String file = filename;
			api.setChunkSize(8192);
			api.setEvent(event);
			api.simpleFileGet("", file, false);
			log.info("File Socket Receive Success !!!");
			return true;
		} catch (Exception e) {
			log.error("File Socket Receive Fail !!!");
			// throw new RuntimeException("File Receive Fail  !!");
			return false;
		}
	}

	public boolean socketReceive(String filename, String agentDir,
			String serverInfo, int fileRcvTryCount) throws Exception {
		int cnt = 0;
		log.debug("File Socekt Receive Start,, RcvTryCount:" + fileRcvTryCount);
		while (cnt < fileRcvTryCount) {
			try {
				IFileEvent event = null;
				String serverTemp[] = serverInfo.split(":");
				String serverIp = serverTemp[0];
				int serverPort = Integer.parseInt(serverTemp[1]);
				log.debug("file server :" + serverIp + " port :" + serverPort);
				IndigoFileTransferAPI api = new IndigoFileTransferAPI(serverIp,
						serverPort);
				api.setRepository(agentDir);
				String file = filename;
				api.setChunkSize(8192);
				api.setEvent(event);
				api.simpleFileGet("", file, false);
				log.info("File Socket Receive Success !!!");
				break;
			} catch (Exception e) {
				log.info("File Socket Receive Fail , try count:" + (cnt + 1));
				e.printStackTrace();
				cnt++;
			}
		}
		if (cnt > fileRcvTryCount) {
			return false;
		} else {
			return true;
		}
	}

	public void afterPropertiesSet() throws Exception {

	}
}
