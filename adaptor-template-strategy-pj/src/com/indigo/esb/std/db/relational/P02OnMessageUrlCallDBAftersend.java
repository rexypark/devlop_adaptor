package com.indigo.esb.std.db.relational;

import java.io.BufferedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageDBSupport;
import com.indigo.esb.std.db.relational.common.DataSet;
import com.indigo.esb.std.db.relational.common.PLCInterfaceInfo;
import com.indigo.esb.std.db.relational.common.PLCQueryStatementType;
import com.indigo.esb.std.db.relational.common.SqlUtil;

public class P02OnMessageUrlCallDBAftersend  extends OnMessageDBSupport {
	
	private String url;
	private int timeout_value;
	
	static final Logger log = LoggerFactory.getLogger(P02OnMessageUrlCallDBAftersend.class);

	public void process(IndigoMessageResult onSignalResult) throws Exception {
		PLCInterfaceInfo interfaceInfo = (PLCInterfaceInfo) onSignalResult.getInterfaceInfo();

		Map<String, Object> mainData = ((DataSet) onSignalResult.getDataObj()).getMainData();
		HttpURLConnection httpUrlConn = null;
		BufferedOutputStream bos = null;
		
		
		try {
			
			mainData.put("DEAL_STATE", "S");
			
			if(url == null)throw new Exception("Required requestUrl");
			URL reqUrl = new URL(url + mainData.get("IF_BID"));
			httpUrlConn = (HttpURLConnection) reqUrl.openConnection();
			httpUrlConn.setConnectTimeout(timeout_value);
			httpUrlConn.setReadTimeout(timeout_value);
			httpUrlConn.setRequestMethod("GET");
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			httpUrlConn.setDefaultUseCaches(false);
			
			//Send Request
			bos = new BufferedOutputStream(httpUrlConn.getOutputStream());
			bos.flush();
			bos.close();
			
			int status = httpUrlConn.getResponseCode();
			
			log.info("호출결과 : " + status);
			
		} catch (Exception ex){
			mainData.put("DEAL_STATE", "F");
			log.error("ERROR : " + ex);
		} finally {
			if(httpUrlConn != null)
				httpUrlConn.disconnect();
			String getSqlId = SqlUtil.getMybaitsSqlId(interfaceInfo.getInterfaceId(), PLCQueryStatementType.CALLABLE_RESULT);
			sqlSession.update(getSqlId, mainData);
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTimeout_value(int timeout_value) {
		this.timeout_value = timeout_value;
	}
	
	
}
