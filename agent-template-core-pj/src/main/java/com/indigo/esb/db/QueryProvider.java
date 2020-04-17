package com.indigo.esb.db;

import java.util.Map;

public interface QueryProvider {
	String getQueryByInterfaceId(String interfaceId,
			QueryStatementType type);

	String getQueryByTableName(String tableName, QueryStatementType type);
	
	Map<String, String> getSendedRowUpdateColumnValueMap();

	
	Map<String, String> getReturnedResultsUpdateColumnValueMap() ;


	Map<String, String> getReceivedRowsDMLColumnValueMap() ;

	String getStatusColumnName();

	String getFailStatusColumnValue();

	String getSuccessStatusColumnValue();

	
}
