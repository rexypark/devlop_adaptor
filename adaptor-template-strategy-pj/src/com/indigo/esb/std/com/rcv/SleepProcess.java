package com.indigo.esb.std.com.rcv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.OnMessageStrategy;

/**
 * @author clupine
 */
public class SleepProcess implements OnMessageStrategy{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	long sleepMili;
	
	public void setSleepMili(long sleepMili) {
		this.sleepMili = sleepMili;
	}
	
	
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		logger.info("Sleep Milisecond : {} " , sleepMili);
		Thread.sleep(sleepMili);
	}

}
