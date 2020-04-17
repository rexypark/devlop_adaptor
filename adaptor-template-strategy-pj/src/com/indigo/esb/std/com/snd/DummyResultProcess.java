package com.indigo.esb.std.com.snd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.OnMessageStrategy;

/**
 * 
 * 
 * @author clupine
 *
 */
public class DummyResultProcess implements OnMessageStrategy{

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void process(IndigoMessageResult indigoMessageResult)
			throws Exception {
		logger.debug("DummyResult Header : {} " , indigoMessageResult.getProperties().getHeaderInfoMap() );
	}

}
