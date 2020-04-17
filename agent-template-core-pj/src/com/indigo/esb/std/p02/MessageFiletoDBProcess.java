package com.indigo.esb.std.p02;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.MyBatisTransactionManager;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.util.DateUtil;
import com.indigo.esb.util.ZipUtils;
import com.indigo.fileserver.client.IndigoFileTransferAPI;
import com.indigo.fileserver.event.IFileEvent;

public class MessageFiletoDBProcess extends OnMessageDBSupport {

	public String path = "../DB_FILE";
	public String serverInfo = "127.0.0.1:24212";
	protected String spitChar = ",";
	private MyBatisTransactionManager tranManager;
	private FileInputStream fileInputStream;
	private String charSet = "euc-kr";
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	public void setTranManager(MyBatisTransactionManager tranManager) {
	    this.tranManager = tranManager;
	}
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		int insertCnt = 0;
		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		log.info("#### OnMessage process Message : " + indigoMessageResult.getProperties().getHeaderInfoMap());
		String getSqlId = getMybaitsSqlId(dbInterfaceInfo.getInterfaceId(), QueryStatementType.INSERT);
		Map<String, String> addDataMap = indigoMessageResult.getProperties().getHeaderInfoMap();
		String fileName = indigoMessageResult.getProperty("tx_id");
		String[] serverInfo = getServerInfo().split("\\,");
		boolean sndOk = true;
		if(indigoMessageResult.getProperty("TIMEOUT") != null){
		    if(isTimeout(indigoMessageResult.getProperty(ESB_TX_ID), Integer.parseInt((indigoMessageResult.getProperty("TIMEOUT"))))){
			String rcvQueue = indigoMessageResult.getMessage().getJMSDestination().toString();
			String dlqName =  rcvQueue.substring(rcvQueue.lastIndexOf('/')+1, rcvQueue.length())+".DLQ";
			log.debug("destination : " + dlqName);
			indigoMessageResult.getInterfaceInfo().setTargetDestinationName(dlqName);
			sendMessage(dlqName, indigoMessageResult);
			log.error("time out error");
			throw new RuntimeException("time out error");
		    }
		}
		for (int i = 0; i < serverInfo.length; i++) {
		    if (socketReceive(fileName + ".zip", path, serverInfo[i])) {
			ZipUtils.decompress(new File(path), new File(path, fileName + ".zip"));
			sndOk=true;
			break;
		    }
		    sndOk = false;
		}
		log.debug("sndOK :" + sndOk);
		if(!sndOk){
		    throw new RuntimeException("File Receive Fail  !!");
		}
		//Map<String, String> proMap = indigoMessageResult.getProperties().getHeaderInfoMap();
		String rcvFileName = indigoMessageResult.getProperty("tx_id");
		if(indigoMessageResult.getProperty("org_file_name") != null){
		    rcvFileName = indigoMessageResult.getProperties().getHeaderInfoMap().get("org_file_name");
		}
		
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(path + File.separator + rcvFileName);
			isr = new InputStreamReader(fis, charSet);
			br = new BufferedReader(isr);
			String line = null;
			
		    String[] column = null; 
		    List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		    int idx = 0;
			while((line = br.readLine()) != null) {
		    	if(idx == 0) {	
		    		column = line.split(getSpitChar());	
		    	} else {
		    		String[] value = line.split(getSpitChar());	
		    		HashMap<String, Object> param = new HashMap<String, Object>();
		    		while (column.length != value.length) {
		    		    line += "\n";
		    		    line += br.readLine();
		    		    value = line.split(",,,");
		    		    if (column.length < value.length) {
		    			throw new Exception("File parsing error");
		    		    }
		    		}
		    		
		    		for (int i = 0; i < value.length; i++) {
		    			if(value[i].equals("null"))value[i]=null;
		    		    param.put(column[i], value[i]);
					}
		    		
		    		dataList.add(param);
		    		
		    		int dataCnt = Integer.parseInt(indigoMessageResult.getProperty("count"));		    		
		    		if (dbInterfaceInfo.getCommitCount() > 0) {	
			    		if(idx % dbInterfaceInfo.getCommitCount() == 0) {
			    			dbRowCommit(getSqlId, dataList);
			    			dataList.clear();
			    		} else if(idx == dataCnt) {
			    			dbRowCommit(getSqlId, dataList);
			    			dataList.clear();
			    		}
		    		} else {	
		    			if(idx == dataCnt) dbRowCommit(getSqlId, dataList);
		    		}
		    	}
		    	idx++;
		    }
		    
		    log.info("Process Insert Count : " + (idx - 1));	
		    
		    new File(path + File.separator + indigoMessageResult.getProperty("tx_id")).delete();
		    
		} catch (Exception e) {
			log.error("Processing error :: "+ e.toString());
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
			} catch (Exception e2) {
				log.error(e2.toString());
			}
		}
		
	}

	public int dbRowCommit(String getSqlId, List<Map<String, Object>> dataList) {
    	int cnt = 0;

    	if(this.tranManager!= null)this.tranManager.start();
    	for (Map<String, Object> dataMap : dataList) {
    		cnt = sqlSession.update(getSqlId, dataMap);
    	}
    	if(this.tranManager!= null)this.tranManager.commit();
    	
    	return cnt;
	}
	
	public boolean socketReceive(String filename, String agentDir, String serverInfo) throws Exception {
		try {

			IFileEvent event = null;
			String serverTemp[] = serverInfo.split(":");
			String serverIp = serverTemp[0];
			int serverPort = Integer.parseInt(serverTemp[1]);
			log.debug("file server :" + serverIp+ " port :" + serverPort);
			IndigoFileTransferAPI api = new IndigoFileTransferAPI(serverIp, serverPort);
			api.setRepository(agentDir); 
			String file = filename;
			api.setChunkSize(8192);
			api.setEvent(event);
			api.simpleFileGet("", file, true);
			log.info("File Socket Receive Success !!!");
			return true;
		} catch (Exception e) {
			log.error("File Socket Receive Fail !!!");
			//throw new RuntimeException("File Receive Fail  !!");
			return false;
		}
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	public String getServerInfo() {
		return serverInfo;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setSpitChar(String spitChar) {
		this.spitChar = spitChar;
	}

	public String getSpitChar() {
		return spitChar;
	}
	public void sendMessage(String queueName, IndigoMessageResult indigoMessageResult){
	    this.jmsTemplate.convertAndSend(queueName,
			indigoMessageResult.getTemplateMessage(""), indigoMessageResult.getProperties());
	}
	public boolean isTimeout(String txId, int timeOut) throws ParseException  {
	    String stTime = txId.substring(txId.lastIndexOf("_")+1, txId.lastIndexOf("_")+15);
	    
	    if(DateUtil.getGapOfSeconds(stTime, DateUtil.getDateTime()) > timeOut ){
		return true;
	    }
	    return false;
	}
	protected void fileReadnInsert(File file, String name)
		throws Exception {}
}
