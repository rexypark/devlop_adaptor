package com.indigo.esb.cj.sap.rcv;

import java.math.BigDecimal;
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
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * TO-BE DB2SAP 패턴 중 RFC Function 결과를 송신측에 전송하는 처리
 * @author clupine
 *
 */
public class ReceiveSapCallerReturnProcess extends OnMessageSpacenameDBSupport {

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
		String tName = properties.getProperty(if_id + ".inTable");

		JCoDestination destination =null;
		JCoFunction function = null;
        try{
    		destination = JCoDestinationManager.getDestinationForIndigo("DESTINATION_CONFIG", properties);
            function = destination.getRepository().getFunction(rfcFunction);
           	JCoParameterList tabledata = function.getTableParameterList();
   			JCoTable iTab = tabledata.getTable(tName);
   			JCoMetaData inTablemetaData =  iTab.getMetaData();
   			
			for (Map<String, Object> dataMap : rowMapList) {
				iTab.appendRow();
				
				for(int j=0;j<iTab.getNumColumns();j++) {
					Object obj = dataMap.get(inTablemetaData.getName(j));
					
					if(obj instanceof BigDecimal){
						iTab.setValue(inTablemetaData.getName(j), ((BigDecimal)obj).toString());
					}else{
						iTab.setValue(inTablemetaData.getName(j), obj);
					}
    			}
			}
    	    
    		function.execute(destination);

			JCoParameterList outList = function.getTableParameterList() ;	
			JCoTable outTable = outList.getTable(properties.getProperty(if_id + ".outTable"));
			JCoMetaData outTableMetaData =  outTable.getMetaData();
			
			for(int i=0;i<outTable.getNumRows();i++) {
				Map<String, Object> map = rowMapList.get(i);
				map.putAll(addDataMap);
				outTable.setRow(i);
    			for(int j=0;j<outTable.getNumColumns();j++) {
					Object obj = outTable.getValue(outTableMetaData.getName(j));
					map.put(outTableMetaData.getName(j), obj);
    			}
			}

		}catch(Exception ex){
			log.error("error : " , ex);
			throw ex;
		}
	}
}