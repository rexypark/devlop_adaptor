package com.indigo.esb.broker.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.indigo.indigomq.broker.BrokerPluginSupport;
import com.indigo.indigomq.broker.ProducerBrokerExchange;
import com.indigo.indigomq.command.Message;
import com.indigo.indigomq.command.MessageDispatch;

@SuppressWarnings("unchecked")
public class HistoryRecordingBrokerPlugin extends BrokerPluginSupport {

	public static Log logger = LogFactory
			.getLog(HistoryRecordingBrokerPlugin.class);

	private boolean logAll = false;

	private boolean logSend = false;

	private boolean logReceive = false;

	@Override
	public void send(ProducerBrokerExchange producerExchange,
			Message messageSend) throws Exception {
		if (logAll || logSend) {
			logger.info("Sending message : " + messageSend.copy());
		}
		super.send(producerExchange, messageSend);
	}

	@Override
	public void postProcessDispatch(MessageDispatch messageDispatch) {
		if (logAll || logReceive) {
			logger.info("postProcessDispatch :" + messageDispatch);
		}
		super.postProcessDispatch(messageDispatch);
	}

	public void setLogAll(boolean logAll) {
		this.logAll = logAll;
	}

	public void setLogSend(boolean logSend) {
		this.logSend = logSend;
	}

	public void setLogReceive(boolean logReceive) {
		this.logReceive = logReceive;
	}

}
