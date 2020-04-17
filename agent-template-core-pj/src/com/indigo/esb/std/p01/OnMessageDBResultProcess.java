package com.indigo.esb.std.p01;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageDBSupport;

public class OnMessageDBResultProcess extends OnMessageDBSupport {

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		log.info("### Result Header : " + indigoMessageResult.getProperties().getHeaderInfoMap());
		Object pojo = indigoMessageResult.getDataObj();
  		List<Map<String, Object>> result = (List<Map<String, Object>>) pojo;
  		log.debug("pojo :" + result);
	}

}
