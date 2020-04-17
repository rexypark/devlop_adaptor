package com.indigo.esb.std.p04;

import org.springframework.beans.factory.annotation.Autowired;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.socket.client.DefaultClientManager;
import com.indigo.esb.adaptor.strategy.socket.OnMessageSocketSupport;
public class OnMessageSocketProcess extends OnMessageSocketSupport {
	@Autowired (required=false)
	protected DefaultClientManager clientManaer;
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		
		try {
				requestMessage(indigoMessageResult);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			
			// TODO: handle exception
		}
	}
/*	 private void returnMessage(String log_state, String err_msg, Map<String, String> propertyMap, Map mapMsg) throws Exception{
		    IndigoHeaderMessagePostProcessor pp = new IndigoHeaderMessagePostProcessor();

		    for (Iterator iterator = propertyMap.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				pp.addHeaderInfo(key, propertyMap.get(key));
				log.debug("key :" + key + " value :" + propertyMap.get(key));
			}
		    pp.addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_TRANS_STATUS, log_state);
		    if(err_msg != null){
			pp.addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_ERR_MSG, err_msg);
		    }
		    pp.addHeaderInfo("esb_if_data_type", InterfaceDataType.RESULT.name());
		    List<Map<String, Object>> result =  new ArrayList();
		    mapMsg.put("RESPCODE", "0000");
		    mapMsg.put("IFTYPE", "MM");
		    if(log_state.equals(IndigoHeaderJMSPropertyConstants.ESB_TRANS_FAIL)){
		    	mapMsg.put("RESPCODE", "9999");
		    }
		    result.add(mapMsg);
		    log.info("################ returnMessage"+pp.getHeaderInfoMap());
		    String returnQueueName = propertyMap.get(IndigoHeaderJMSPropertyConstants.ESB_TARGET_DESTINATION);
	        jmsTemplate.convertAndSend(returnQueueName, result, pp);
		}*/
}
