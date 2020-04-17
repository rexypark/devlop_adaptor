package com.indigo.esb.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.indigo.esb.config.InterfaceInfo;

public class TableMetaDataInfo implements InitializingBean {

	protected final String columnName = "COLUMN_NAME";
	protected final String columnType = "DATA_TYPE";

	protected SqlSessionTemplate sqlSession;
	protected List<InterfaceInfo> interfaceList;
	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected DatabaseMetaData metaData;

	@Override
	public void afterPropertiesSet() throws Exception {

		metaData = getSqlSession().getSqlSessionFactory().openSession().getConnection().getMetaData();

		/*StopWatch sw = new StopWatch(getClass().getName());
		Assert.notNull(interfaceList, "Property 'interfaceList' is Required");
		log.info("Table Columns Information Init Start");
		for (InterfaceInfo info : getInterfaceList()) {
			DBInterfaceInfo dbInfo = (DBInterfaceInfo) info;

			sw.start(info.getInterfaceId());
			if (dbInfo.getTableName() != null) {
				dbInfo.setTableInfo(getTableInfo(dbInfo.getTableName()));
				sw.stop();
			}
			log.info("Table Columns Information Init End {}", sw.toString());
		}
*/
	}

	public Map<String, Object> getTableInfo(String tableName) throws Exception {
		Map<String, Object> tableInfoMap = new HashMap<String, Object>();
		List<Map<String, Object>> colList = new ArrayList<Map<String, Object>>();
		ResultSet rs = metaData.getColumns(null, null, tableName, null);
		while (rs.next()) {
			Map<String, Object> colMap = new HashMap<String, Object>();
			String name = rs.getString(columnName).toUpperCase();// COLUMN_NAME
			int type = new Integer(rs.getInt(columnType));// DATA_TYPE
															// int
			colMap.put(columnName, name);
			colMap.put(columnType, type);
			colList.add(colMap);

		}
		rs.close();
		ArrayList<String> pkList = new ArrayList<String>();
		rs = metaData.getPrimaryKeys(null, null, tableName);// java
		while (rs.next()) {
			pkList.add(rs.getString(columnName));
		}
		rs.close();
		tableInfoMap.put("TABLE_NAME", tableName);
		tableInfoMap.put("COLINFO_LIST", colList);
		tableInfoMap.put("PK_LIST", pkList);

		return tableInfoMap;
	}

	public SqlSessionTemplate getSqlSession() {
		return sqlSession;
	}

	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}

	public List<InterfaceInfo> getInterfaceList() {
		return interfaceList;
	}

	public void setInterfaceList(List<InterfaceInfo> interfaceList) {
		this.interfaceList = interfaceList;
	}

}
