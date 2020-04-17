package com.indigo.esb.broker.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.indigo.indigomq.broker.Broker;
import com.indigo.indigomq.broker.BrokerFilter;
import com.indigo.indigomq.broker.Connection;
import com.indigo.indigomq.broker.ConnectionContext;
import com.indigo.indigomq.broker.region.MessageReference;

public class HistoryRecordingBrokerFilter extends BrokerFilter {

	public static Log logger = LogFactory
			.getLog(HistoryRecordingBrokerFilter.class);

	public HistoryRecordingBrokerFilter(Broker next) {
		super(next);
	}

	@Override
	public void messageConsumed(ConnectionContext context,
			MessageReference msgRef) {
		logger.warn("되는거야이거?");
		if (logger.isInfoEnabled()) {
			Connection conn = context.getConnection();
			logger.info("빼갔네. HistoryRecording [Message Consumed] - consumed message:"
					+ (msgRef != null ? msgRef.getMessage() : null)
					+ " ClientId:"
					+ context.getClientId()
					+ " Connection:"
					+ conn);
		}

		super.messageConsumed(context, msgRef);
	}

	@Override
	public void messageDelivered(ConnectionContext context,
			MessageReference msgRef) {
		logger.warn("되는거야이거?");
		if (logger.isInfoEnabled()) {
			Connection conn = context.getConnection();
			logger.info("받았네. HistoryRecording [Message Delivered] - delivered message:"
					+ (msgRef != null ? msgRef.getMessage() : null)
					+ " ClientId:"
					+ context.getClientId()
					+ " Connection:"
					+ conn);

		}

		super.messageDelivered(context, msgRef);
	}

}
