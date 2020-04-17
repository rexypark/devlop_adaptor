package com.indigo.esb.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

public class SqlmapClientQueryProvider extends AbstractQueryProvider implements
		InitializingBean {

	private static final Log log = LogFactory
			.getLog(SqlmapClientQueryProvider.class);

	@Override
	public String getQueryByInterfaceId(String interfaceId,
			QueryStatementType type) {
		if (interfaceId == null) {
			throw new IllegalArgumentException("interfaceId must be set");
		}
		String queryId = getQuery(interfaceId.toLowerCase(), type);
		log.debug("queryId:" + queryId);
		return queryId;
	}

	@Override
	public String getQueryByTableName(String tableName, QueryStatementType type) {
//		if (tableName == null) {
//			throw new IllegalArgumentException("tableName must be set");
//		}
		String queryId = getQuery(tableName.toLowerCase(), type);
		log.debug("queryId:" + queryId);
		return queryId;
	}

	public String getQuery(String postfix, QueryStatementType type) {
		switch (type) {
		case SELECT:
			return "select_" + postfix;
		case INSERT:
			return "insert_" + postfix;
		case UPDATE:
			return "update_" + postfix;
		case DELETE:
			return "delete_" + postfix;
		case MERGE:
			return "merge_" + postfix;
		case SOURCE_UPDATE:
			return "sourceUpdate_" + postfix;
		case SOURCE_RESULT_UPDATE:
			return "sourceResultUpdate_" + postfix;
		default:
			throw new IllegalArgumentException("Unsupported QueryStatementType");
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
	}
}
