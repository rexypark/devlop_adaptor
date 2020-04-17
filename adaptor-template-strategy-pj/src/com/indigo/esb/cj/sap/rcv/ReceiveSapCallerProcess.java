package com.indigo.esb.cj.sap.rcv;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;

import jeus.util.LinkedHashMap;

import org.apache.commons.lang.time.DateFormatUtils;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;



/**
 * 사용 DB 수신 처리
 * 
 * @author clupine
 *
 */
public class ReceiveSapCallerProcess extends OnMessageSpacenameDBSupport {

	@Resource(name="sapConfig")
	private Properties properties;
    StringBuffer data = new StringBuffer();

	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {
		
		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> rowMapList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		//log.debug("1.rowMapList=====================================>"+rowMapList.toString());

//		Map<String, String> addDataMap = new java.util.HashMap<String, String>();

//		addDataMap.put(dbInterfaceInfo.getIfUuidColName(), if_uuid);
//		addDataMap.put(dbInterfaceInfo.getTimeColName(), DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS"));
		
		String if_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_IF_ID);		
		String rfcFunction = properties.getProperty(if_id + ".function");
		String inTable = properties.getProperty(if_id + ".inTable");

		String KEY_NAME = (String)properties.getProperty("DB2SAP.KEY_NAME");
		String sValue = (String)properties.getProperty(if_id+".sValue");
		String eValue = (String)properties.getProperty(if_id+".eValue");
		String code = "";
		String message ="";
		
		log.debug("uuid :  " + KEY_NAME);
		
		JCoDestination destination =null;
		JCoFunction function = null;
        try{
    		destination = JCoDestinationManager.getDestinationForIndigo("DESTINATION_CONFIG", properties);
            function = destination.getRepository().getFunction(rfcFunction);
           	JCoParameterList tabledata = function.getTableParameterList();
   			JCoTable iTab = tabledata.getTable("IN_TABLE"); //Table Name Set

			for (Map<String, Object> dataMap : rowMapList) {//Row
				
//				Object eaitxid =  dataMap.get("EAI_TX_ID");
//				Object eaidate =  dataMap.get("EAI_DATE");
				Object uuid =  dataMap.get(KEY_NAME);
				
				dataMap.remove(KEY_NAME);
				dataMap.remove("EAI_TX_ID");
				dataMap.remove("EAI_DATE");
				
//				log.debug("data : " + dataMap);
	        	iTab.appendRow();
	        	
	            int idx=0;
		        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
		        	
		            String key = entry.getKey();
		            Object value = entry.getValue();
	            	iTab.setValue(idx,value);
			        log.debug("==========================="+key+"==>"+value);
    				
    				idx++;
		   	    }
		        
		        log.debug("set Count : " + idx);
		        log.debug("dataMap Count" + dataMap.size());
		        
//		        dataMap.put("EAI_TX_ID", eaitxid);
//		        dataMap.put("EAI_DATE", eaidate);
		        dataMap.put(KEY_NAME, uuid);
			}
			    		
    	    tabledata.setValue("IN_TABLE",iTab);
    	    log.debug("================================>iTab====> "+ iTab);
			//RFC Function Execution
    		function.execute(destination);

    		/*export Struct*/
			JCoParameterList outParamList = function.getExportParameterList();
			JCoStructure outParam = outParamList.getStructure("OUT_RETURN");
			code = outParam.getString("CODE");
			message =outParam.getString("MESSAGE");

			log.debug(" >> ==== Code    =========>"+code);
			log.debug(" >> ==== Message =========>"+message);
	    	
	    	for (Map<String, Object> dataMap : rowMapList) {
	    		if("01".equals(code)) {	    			
	    			dataMap.put("SCODE",sValue );
	    			dataMap.put("MESSAGE", "[SAP]CODE : "+code+" | "+message);
	    			//dataMap.put("TRANS_SEQ",dataMap.get(trans_seq) );
	    			//dataMap.put("IF_UUID",if_uuid );
		    	} else if(code.equals("02") || code.equals("03")) {
		    		dataMap.put("SCODE",eValue );
		    		dataMap.put("MESSAGE", "[SAP]CODE : "+code+" | "+message);
		    		//dataMap.put("TRANS_SEQ",trans_seq );
		    		//dataMap.put("IF_UUID",if_uuid );
		    	}
			}
   			//log.debug("2.dataMap.toString()================> "+rowMapList.toString());

	    	if(code.equals("02") || code.equals("03")) {
	    		throw new Exception("result Code :" + code+"|"+message);
	    	}

			if(code.equals("02"))
				throw new Exception();
			
//			addDataMap.put(dbInterfaceInfo.getIfUuidColName(), if_uuid);
//			addDataMap.put(dbInterfaceInfo.getTxIdColName(), tx_id);

		}catch(Exception ex){
	    	for (Map<String, Object> dataMap : rowMapList) {
    			dataMap.put("SCODE",eValue);
    			dataMap.put("MESSAGE", "[SAP]CODE : "+ code+" | "+message);
			}
			log.error("error : " , ex);
			throw ex;
		}finally{
			if(destination != null){
				try {
					JCoContext.end(destination);
				} catch (JCoException e) {
					log.error("error : " , e);
				}
				destination.removeThroughput();
				destination = null;
			}
		}        
	}
	
	
	//test
	public static void main(String[] args) {
		Map<String,String> map = new LinkedHashMap();
		
		map.put("AAA", "111111");
		map.put("BBB", "111111");
		map.put("CCC", "111111");
		map.put("DDD", "111111");
		map.put("EEE", "111111");
		map.put("FFF", "111111");
		map.put("GGG", "111111");
		map.put("HHH", "111111");
		
		map.remove("BBB");
		map.remove("DDD");
		map.remove("HHH");
		
		Set<Entry<String,String>> keySet = map.entrySet();
		for (Entry<String,String> key : keySet) {
			System.out.println(key);
		}
	}
	
}

