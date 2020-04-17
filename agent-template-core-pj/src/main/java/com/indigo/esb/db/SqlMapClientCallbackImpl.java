package com.indigo.esb.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientCallback;

import com.ibatis.sqlmap.client.SqlMapExecutor;


public class SqlMapClientCallbackImpl implements SqlMapClientCallback<int[]> {

	public List<Map<String, Object>> rowMapList;
	public Map<String, String> addDataMap;
	public String sqlOrQueryId;
	
	public SqlMapClientCallbackImpl(String sqlOrQueryId, Map<String, String> addDataMap, List<Map<String, Object>> rowMapList){
		this.sqlOrQueryId = sqlOrQueryId;
		this.addDataMap = addDataMap;
		this.rowMapList = rowMapList;
	}

	@Override
	public int[] doInSqlMapClient(SqlMapExecutor executor) throws SQLException {

		int[] returns = new int[rowMapList.size()];
		int i = 0;
		executor.startBatch();
		for (Map<String, Object> rowMap : rowMapList) {
			rowMap.putAll(addDataMap);
			int ret = executor.update(sqlOrQueryId, rowMap);
			returns[i++] = ret;
		}
		executor.executeBatch();
		return returns;
	
	}

}
