package com.indigo.esb.std.p05;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryProvider;
import com.indigo.esb.db.QueryStatementType;

public class P05OnSignalDBPoll extends P05OnsignalDBSupport{
	
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {
		DBInterfaceInfo interfaceInfo = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();

		String sql = queryProvider.getQueryByInterfaceId(
				interfaceInfo.getInterfaceId(), QueryStatementType.SELECT);
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		/*List<Map<String, Object>> result2 = new ArrayList();
		for (Map<String, Object> map : result) {
			LinkedHashMap newMap = new LinkedHashMap<String, Object>();
			newMap.putAll(map);
			result2.add(newMap);
		}*/
		onSignalResult.setPollResultDataObj(result);
		if (result != null) {
			onSignalResult.setResultCount(result.size());
			log.info("onSinalResult : "  + onSignalResult.toString());
		}
		
	}



	@Override
	public void afterPropertiesSet() throws Exception {
		
	}



	public void setQueryProvider(QueryProvider queryProvider) {
		this.queryProvider = queryProvider;
	}



	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	
}
