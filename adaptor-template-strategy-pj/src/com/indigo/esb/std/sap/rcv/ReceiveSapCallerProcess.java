package com.indigo.esb.std.sap.rcv;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

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
		Map<String, String> addDataMap = new java.util.HashMap<String, String>();

		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);

		addDataMap.put(dbInterfaceInfo.getTxIdColName(), tx_id);
		addDataMap.put(dbInterfaceInfo.getTimeColName(), DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS"));
		
		String if_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_IF_ID);		
		String rfcFunction = properties.getProperty(if_id + ".function");
		String inTable = properties.getProperty(if_id + ".inTable");

		String KEY_NAME = (String)properties.getProperty("DB2SAP.KEY_NAME");
		String sValue = (String)properties.getProperty(if_id+".sValue");
		String eValue = (String)properties.getProperty(if_id+".eValue");
		String code = "";
		String message ="";
		
		log.info("uuid :============================================  " + KEY_NAME);
		
		JCoDestination destination =null;
		JCoFunction function = null;
        try{
    		destination = JCoDestinationManager.getDestinationForIndigo("DESTINATION_CONFIG", properties);
            function = destination.getRepository().getFunction(rfcFunction);
           	JCoParameterList tabledata = function.getTableParameterList();
   			JCoTable iTab = tabledata.getTable("IN_TABLE"); //Table Name Set

			for (Map<String, Object> dataMap : rowMapList) {//Row
				
				Object eaitxid =  dataMap.get("EAI_TX_ID");
				Object eaidate =  dataMap.get("EAI_DATE");
				Object uuid =  dataMap.get(KEY_NAME);
				
				dataMap.remove(KEY_NAME);
				dataMap.remove("EAI_TX_ID");
				dataMap.remove("EAI_DATE");
				
				log.info("data : " + dataMap);
	        	iTab.appendRow();
	            int idx=0;
		        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
		        	
		            String key = entry.getKey();
		            Object value = entry.getValue();
	    				iTab.setValue(idx,value);
	    				idx++;
		   	    }
		        dataMap.put("EAI_TX_ID", eaitxid);
		        dataMap.put("EAI_DATE", eaidate);
//		        log.info("===========================END");
			}
			    		
    	    tabledata.setValue("IN_TABLE",iTab);
    	    log.info("================================>iTab====>"+iTab);
			//RFC Function Execution
    		function.execute(destination);

    		/*export Table*/
			int check_count =0;
			JCoParameterList outTable = function.getTableParameterList() ;	
 
    			JCoTable outList = outTable.getTable("T_RESULT");
				check_count = outList.getNumRows();
				log.info("OutTable RowNum=========>"+check_count);
    			for(int i=0;i<check_count;i++) { //Row
				    outList.setRow(i);
	    			for(int j=1;j<outList.getNumColumns();j++) {  //Field
		    			log.info("outTableArr==>"+outList.getString("FLAG")+"::"+outList.getString("MESSAGE"));
	    				outList.getString("FLAG");
	    				outList.getString("MESSAGE");
//						data.append(fieldDelimeter);
	    			}
//					data.append(rowDelimeter);
    			}
//				data.append(tableDelimeter);
			
			


		}catch(Exception ex){

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
}
