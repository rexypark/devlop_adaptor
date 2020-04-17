package com.indigo.esb.std.file.rcv;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_TX_ID;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.file.OnMessageFileSupport;
import com.indigo.esb.config.FileInterfaceInfo;
import com.indigo.esb.util.DateUtil;
import com.indigo.esb.util.ZipUtils;
/**
 * �޽��� ���� �� �ش� ������ ESB������ ���� ����� ���·� �����ϰ�
 * tmpDir�� ������������ ��
 * �޽�������� trg_path�� �ִ� ��� �ش� path�� �����ϰ�
 * ��°�� rcvDir�� ������ ���� �����Ѵ�
 * ������ ����Ǹ� tmpDir�� �ִ� ������ �����Ѵ� 
 * @author Administrator
 *
 */
public class OnMessageFileProcess extends OnMessageFileSupport {

	public String serverInfo;

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		FileInterfaceInfo fileInterfaceInfo = (FileInterfaceInfo) indigoMessageResult.getInterfaceInfo();
		String receiveDir = fileInterfaceInfo.getReceiveDir();
		String tempDir = fileInterfaceInfo.getTmpDir();
		String errDir = fileInterfaceInfo.getErrDir();
		log.info("#### OnMessage process Message : " + indigoMessageResult.getProperties().getHeaderInfoMap());
		/*if(isTimeout(indigoMessageResult.getProperty(ESB_TX_ID), Integer.parseInt((indigoMessageResult.getProperty("TIMEOUT"))))){
  		    String rcvQueue = indigoMessageResult.getMessage().getJMSDestination().toString();
  		    String dlqName =  rcvQueue.substring(rcvQueue.lastIndexOf('/')+1, rcvQueue.length())+".DLQ";
  		    log.debug("destination : " + dlqName);
  		    sendMessage(dlqName, indigoMessageResult);
  		    log.error("time out error");
  		    throw new RuntimeException("time out error");
  		}*/
		if (checkedDir(receiveDir) && checkedDir(tempDir) && checkedDir(errDir)) {
		}

		String fileName = indigoMessageResult.getProperty(ESB_TX_ID);
		String[] svrInfo = getServerInfo().split("\\,");
		for (int j = 0; j < svrInfo.length; j++) {
			if (socketReceive(fileName + ".zip", tempDir, svrInfo[j])) {//receive from sockerserver hub to temp
				if (indigoMessageResult.getProperty("trg_path") != null) {
					ZipUtils.decompress(
							new File(indigoMessageResult.getProperty("trg_path")), new File(tempDir, fileName + ".zip"));
				} else {
					//Default decompress from temp -> receive Dir
					ZipUtils.decompress(new File(receiveDir), new File(tempDir, fileName + ".zip"));
				}
				new File(tempDir, fileName + ".zip").delete();
				return;
			}
		}
		throw new RuntimeException("File Receive Fail  !!");

	}

	public boolean checkedDir(String dir) throws IOException {
		if (!new File(dir).exists()) {
			FileUtils.forceMkdir(new File(dir));
			return true;
		} else {
			return true;
		}
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	public String getServerInfo() {
		return serverInfo;
	}
	public void sendMessage(String queueName, IndigoMessageResult indigoMessageResult){
	    this.jmsTemplate.convertAndSend(queueName,
			indigoMessageResult.getTemplateMessage(""), indigoMessageResult.getProperties());
	}
	public boolean isTimeout(String txId, int timeOut) throws ParseException  {
	    String stTime = txId.substring(txId.lastIndexOf("_")+1, txId.lastIndexOf("_")+15);
	    //stTime = DateUtil.addSeconds(timeout);
	    if(DateUtil.getGapOfSeconds(stTime, DateUtil.getDateTime()) > timeOut ){
		return true;
	    }
	    return false;
	}
}
