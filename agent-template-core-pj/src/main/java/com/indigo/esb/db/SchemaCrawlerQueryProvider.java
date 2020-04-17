package com.indigo.esb.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

import schemacrawler.schema.Column;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.Trigger;

import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.config.InterfaceInfo;
import com.indigo.esb.db.query.SchemaCrawlerQueryMaker;

public class SchemaCrawlerQueryProvider extends AbstractQueryProvider implements
		InitializingBean {

	private static final Log logger = LogFactory
			.getLog(SchemaCrawlerQueryProvider.class);

	private String tableNamePattern;

	private Map<String, NamedParamDMLHolder> interfaceQueryMap = new HashMap<String, NamedParamDMLHolder>();

	private Map<String, NamedParamDMLHolder> tableQueryMap = new HashMap<String, NamedParamDMLHolder>();

	private String sourceSelectWhere;

	private String sourceSelectOrderby;
	
	private int rowCnt = 100;;

	private DataSource dataSource;

	public void generateQuery() {
		logger.info("generateQuery call..");
		Connection connection = DataSourceUtils.getConnection(dataSource);
		collectDatabaseMetadata(connection, schemaNamePattern, tableNamePattern);
		DataSourceUtils.releaseConnection(connection, dataSource);
		logger.info(database.getDatabaseInfo());
		logger.info("Schemas : " + database.getSchemas().length);
		SchemaCrawlerQueryMaker qm = new SchemaCrawlerQueryMaker();
		for (final Schema schema : database.getSchemas()) {
			logger.info(schema);
			for (final Table table : schema.getTables()) {
				logger.info("o--> " + table);
				for (final Column column : table.getColumns()) {
					logger.info("     o--> " + column);
				}
				PrimaryKey pk = table.getPrimaryKey();
				logger.info("o-->PK:" + pk);
				IndexColumn[] pkCols = pk.getColumns();
				for (final IndexColumn idxCol : pkCols) {
					logger.info("     o--> " + idxCol);
				}

				Trigger[] triggers = table.getTriggers();
				for (final Trigger trigger : triggers) {
					logger.info("o-->Trigger:" + trigger);
				}

				Table[] tables = table
						.getRelatedTables(TableRelationshipType.child);
				for (final Table child : tables) {
					logger.info("o-->Child Table:" + child);
				}
				logger.info("");

				NamedParamDMLHolder dmls = new NamedParamDMLHolder();
				dmls.setSelect(qm.makeSelectString(table, sourceSelectWhere,
						sourceSelectOrderby, rowCnt));
				dmls.setSourceUpdate(qm.makeSourceUpdate(table,
						sendedRowUpdateColumnValueMap));
				dmls.setSourceResultUpdate(qm.makeSourceResultUpdate(table,
						returnedResultsUpdateColumnValueMap));
				dmls.setInsert(qm.makeInsertString(table,
						receivedRowsDMLColumnValueMap));
				dmls.setUpdate(qm.makeUpdateString(table,
						receivedRowsDMLColumnValueMap));
				dmls.setDelete(qm.makeDeleteString(table));
				dmls.setMerge(qm.makeMergeString(table,
						receivedRowsDMLColumnValueMap));
				logger.info(table.getName() + " 자동 생성 쿼리:" + dmls);
				String interfaceId = findInterfaceId(table);
				if (interfaceId != null)
					interfaceQueryMap.put(interfaceId, dmls);
				tableQueryMap.put(getShortTableName(table), dmls);
			}// end of tables
		}// end of schemas
			// Collections.unmodifiableMap(interfaceQueryMap);
		// Collections.unmodifiableMap(tableQueryMap);
	}

	//@Override
	public String getQueryByInterfaceId(String interfaceId,
			QueryStatementType type) {
		// if(!interfaceQueryMap.containsKey(interfaceId)){
		// generateQuery();
		// }
		logger.debug("interfaceQueryMap : "  + this.tableQueryMap);
		logger.debug("interfaceId : "  + interfaceId);
		NamedParamDMLHolder holder = this.interfaceQueryMap.get(interfaceId);
		return getQuery(holder, type);
	}

	//@Override
	public String getQueryByTableName(String tableName, QueryStatementType type) {
		if (!tableQueryMap.containsKey(tableName)) {
			this.tableNamePattern = ".*\\." + tableName;
			this.generateQuery();
		}
		logger.debug("tableQueryMap : "  + this.tableQueryMap);
		NamedParamDMLHolder holder = this.tableQueryMap.get(tableName);
		return getQuery(holder, type);
	}

	public String getQuery(NamedParamDMLHolder holder, QueryStatementType type) {
		switch (type) {
		case SELECT:
			return holder.getSelect();
		case INSERT:
			return holder.getInsert();
		case UPDATE:
			return holder.getUpdate();
		case DELETE:
			return holder.getDelete();
		case MERGE:
			return holder.getMerge();
		case SOURCE_UPDATE:
			return holder.getSourceUpdate();
		case SOURCE_RESULT_UPDATE:
			return holder.getSourceResultUpdate();
		default:
			throw new IllegalArgumentException("Unsupported QueryStatementType");
		}
	}

	private String findInterfaceId(Table table) {
		if(interfaceList == null) return null;
		String tName = getShortTableName(table);
		String interfaceId = null;
		for (InterfaceInfo info : interfaceList) {
			String ifTableName = ((DBInterfaceInfo) info).getSourceTableName();
			if (tName.equalsIgnoreCase(ifTableName)) {
				interfaceId = info.getInterfaceId();
				break;
			}
		}
		return interfaceId;
	}

	private String getShortTableName(Table table) {
		String tName = table.getName().replaceFirst(table.getSchema() + "\\.",
				"");
		return tName;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(dataSource, "Property 'dataSource' 반드시 필요함");
		boolean sendInterfaceContained = false;
		boolean recvInterfaceContained = false;
		for (InterfaceInfo info : interfaceList) {
			/*if (info.isSend()) {
				sendInterfaceContained = true;
			}
			if (!info.isSend()) {
				recvInterfaceContained = true;
			}*/
		}
		if (sendInterfaceContained) {
			Assert.notNull(sourceSelectWhere,
					"Property 'sourceSelectWhere' 반드시 필요함");
			if (sourceSelectOrderby == null)
				logger.warn("Property 'sourceSelectOrderby' 사용하지 않음.");
			Assert.notNull(sendedRowUpdateColumnValueMap,
					"Property 'sendedRowUpdateColumnValueMap' 반드시 필요함");
			if (returnedResultsUpdateColumnValueMap == null)
				logger.warn("Property 'returnedResultsUpdateColumnValueMap' 사용하지 않음.");
		}

		if (recvInterfaceContained) {
			Assert.notNull(receivedRowsDMLColumnValueMap,
					"Property 'returnedResultsUpdateColumnValueMap' 사용하지 않음.");
		}
		StringBuilder sb = new StringBuilder();
		for (InterfaceInfo info : interfaceList) {
			if (info instanceof DBInterfaceInfo) {
				DBInterfaceInfo dbInfo = (DBInterfaceInfo) info;
				if (dbInfo.getSourceTableName() != null) {
					sb.append(".*\\.");
					sb.append(dbInfo.getSourceTableName());
					sb.append("|");
				}
				if (dbInfo.getTargetTableName() != null
						|| dbInfo.getTargetTableName().equalsIgnoreCase(
								dbInfo.getSourceTableName())) {
					sb.append(".*\\.");
					sb.append(dbInfo.getTargetTableName());
					sb.append("|");
				}
			}
		}
		if (sb.lastIndexOf("|") > 0) {
			sb.deleteCharAt(sb.lastIndexOf("|"));
		} else {
			sb.append(".*\\..*");
		}
		tableNamePattern = sb.toString();
		logger.info("연계 테이블 네임 패턴:" + tableNamePattern);
		this.generateQuery();
	}

	public String getTableNamePattern() {
		return tableNamePattern;
	}

	public void setTableNamePattern(String tableNamePattern) {
		this.tableNamePattern = tableNamePattern;
	}

	public String getSourceSelectWhere() {
		return sourceSelectWhere;
	}

	public void setSourceSelectWhere(String sourceSelectWhere) {
		this.sourceSelectWhere = sourceSelectWhere;
	}

	public String getSourceSelectOrderby() {
		return sourceSelectOrderby;
	}

	public void setSourceSelectOrderby(String sourceSelectOrderby) {
		this.sourceSelectOrderby = sourceSelectOrderby;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setRowCnt(int rowCnt) {
		this.rowCnt = rowCnt;
	}

}
