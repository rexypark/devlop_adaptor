package com.indigo.esb.std.schedule;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.util.SerializationUtils;

import com.indigo.esb.signal.SignalListener;
import com.indigo.esb.std.RMessage;

public class IntefaceRequest implements SignalListener {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	String msgFileBackupPath = null;

	JmsTemplate jmsTemplate;

	public void setMsgFileBackupPath(String msgFileBackupPath) {
		this.msgFileBackupPath = msgFileBackupPath;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	boolean remoteNormal = true;

	boolean failReprocessing = true;

	public void setRemoteNormal(boolean remoteNormal) {
		this.remoteNormal = remoteNormal;
	}

	public void setFailReprocessing(boolean failReprocessing) {
		this.failReprocessing = failReprocessing;
	}

	long remoteNomalTTL = 10000;
	long failReprocessingTTL = 0;

	public void setRemoteNomalTTL(long remoteNomalTTL) {
		this.remoteNomalTTL = remoteNomalTTL;
	}

	public void setFailReprocessingTTL(long failReprocessingTTL) {
		this.failReprocessingTTL = failReprocessingTTL;
	}

	@Override
	public void onSignal(final String if_id) {

		final String[] ss = if_id.split("\\^");
		long oldttl = jmsTemplate.getTimeToLive();

		if (remoteNormal) {
//			synchronized (jmsTemplate) {
				jmsTemplate.setTimeToLive(remoteNomalTTL);
				jmsTemplate.convertAndSend(ss[1], "Adaptor Template Call", new MessagePostProcessor() {

					@Override
					public Message postProcessMessage(Message msg) throws JMSException {
						msg.setStringProperty("if_data_type", "NORMAL");
						msg.setStringProperty("if_id", ss[0]);
						return msg;
					}
				});
				jmsTemplate.setTimeToLive(oldttl);
//			}
		}
		
		oldttl = jmsTemplate.getTimeToLive();
		if (failReprocessing) {
//			synchronized (jmsTemplate) {
				File dir = new File(msgFileBackupPath);
				File[] reprocessFiles = dir.listFiles();
				logger.info("재처리 파일 수량 : " + reprocessFiles.length);

				for (final File reFile : reprocessFiles) {
					logger.info("재처리 파일: " + reFile.getName());
					try {
						byte[] data = FileUtils.readFileToByteArray(reFile);
						final RMessage rm = (RMessage) SerializationUtils.deserialize(data);
						final Map<String,Object> header = rm.getHeader();
						logger.info("재처리 헤더 : " + header);
						
						jmsTemplate.setTimeToLive(failReprocessingTTL);
						jmsTemplate.send(ss[2], new MessageCreator() {
							@Override
							public Message createMessage(Session sess) throws JMSException {
								ObjectMessage msg = sess.createObjectMessage();
								Set<String> keySet = header.keySet();
								for (String key : keySet) {
									msg.setStringProperty(key, (String)header.get(key));
								}
								msg.setObject((Serializable) rm.getData());
								return msg;
							}
						});
						logger.info("재처리 송신완료: " + rm);
						FileUtils.forceDelete(reFile);
					} catch (IOException e) {
						logger.error("Reprocessing Exception : ", e);
					}
//				}
				jmsTemplate.setTimeToLive(oldttl);
			}
		}
	}
}
