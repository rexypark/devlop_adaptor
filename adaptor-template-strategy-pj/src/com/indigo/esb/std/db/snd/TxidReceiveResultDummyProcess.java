package com.indigo.esb.std.db.snd;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;

/**
 *  송신측 결과 처리 하지않고 출력만
 * @author clupine
 *
 */
public class TxidReceiveResultDummyProcess extends OnMessageSpacenameDBSupport {
 
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		log.info("ESB Result Header : " + indigoMessageResult.getProperties().getHeaderInfoMap().toString());

	}

}
