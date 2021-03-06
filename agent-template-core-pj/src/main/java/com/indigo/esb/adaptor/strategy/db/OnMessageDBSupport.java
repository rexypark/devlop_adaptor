package com.indigo.esb.adaptor.strategy.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.strategy.OnMessageDBStrategy;
import com.indigo.esb.db.QueryStatementType;

public abstract class OnMessageDBSupport implements OnMessageDBStrategy, InitializingBean {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired(required = false)
	protected JmsTemplate jmsTemplate;
	@Autowired(required = false)
	protected SqlSessionTemplate sqlSession;

	protected List<Map<String, Object>> makeResult(String colNamesStr, List<Map<String, Object>> recvList) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		String[] colNames = null;
		if (colNamesStr != null) {
			colNames = colNamesStr.split(",");
			Map<String, Object> resultMap = null;
			for (Map<String, Object> recordMap : recvList) {
				resultMap = new HashMap<String, Object>();
				for (String colName : colNames) {
					if (recordMap.containsKey(colName)) {
						resultMap.put(colName, recordMap.get(colName));
					}
				}
				resultList.add(resultMap);
			}
		}
		return resultList;

	}

	protected String getMybaitsSqlId(String interfaceId, QueryStatementType insert) {
		StringBuffer sb = new StringBuffer();
		sb.append(interfaceId);
		sb.append("_");
		sb.append(insert);
		return sb.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

}
