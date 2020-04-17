package com.indigo.esb.adaptor.strategy;

import com.indigo.esb.adaptor.context.IndigoMessageResult;

public interface OnMessageStrategy {
	void process(IndigoMessageResult indigoMessageResult) throws Exception;
}
