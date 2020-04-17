package com.indigo.esb.route;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ibatis.session.SqlSession;

public class DatabaseConfiguration extends AbstractConfiguration {
	protected transient Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);
	Map<String, Object> data = new HashMap<String, Object>();
	public final static String DATA_ARR_QUEUE = "DATA_ARR_QUEUE";
	public final static String RESULT_ARR_QUEUE = "RESULT_ARR_QUEUE";

	@Override
	protected void addPropertyDirect(String arg0, Object arg1) {

	}

	public DatabaseConfiguration(SqlSession sqlSession) {
		logger.info("DatabaseConfiguration init Start");
		List<Map<String, Object>> routeList = sqlSession.selectList("ROUTE_SELECT");
		StringBuffer dataQueueSb = new StringBuffer();
		StringBuffer resultQueueSb = new StringBuffer();
		int i = 0;
		dataQueueSb.append("ROUTE.IN.OTO");
		resultQueueSb.append("RETURN.IN.OTO");
		for (Map<String, Object> data : routeList) {
			dataQueueSb.append(",");
			resultQueueSb.append(",");
			dataQueueSb.append(data.get(DATA_ARR_QUEUE));
			resultQueueSb.append(data.get(RESULT_ARR_QUEUE));
			if (routeList.size() - 1 > i) {
				dataQueueSb.append(",");
				resultQueueSb.append(",");
			}
			i++;
		}

		data.put(DATA_ARR_QUEUE, dataQueueSb.toString());
		data.put(RESULT_ARR_QUEUE, resultQueueSb.toString());
		logger.info("SA ROUTE Propertie : " + data.toString());
		logger.info("DatabaseConfiguration init End");
	}

	@Override
	public boolean containsKey(String key) {
		return true;
	}

	@Override
	public Iterator<String> getKeys() {
		data.put("esb.brokerUrl", "111");
		data.put("esb.route.in.name", "111");
		data.put("esb.return.in.name", "111");
		data.put("ddd", "111");
		data.put("eee", "111");
		Iterator<String> it = data.keySet().iterator();
		return it;
	}

	@Override
	public Object getProperty(String key) {
		return data.get(key);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

}
