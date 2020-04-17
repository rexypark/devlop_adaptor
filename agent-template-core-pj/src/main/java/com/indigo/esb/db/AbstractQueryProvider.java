package com.indigo.esb.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.indigo.esb.config.InterfaceInfo;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

public abstract class AbstractQueryProvider implements QueryProvider,
		InitializingBean {

	private static final Log logger = LogFactory
			.getLog(AbstractQueryProvider.class);

	protected String schemaNamePattern;

	protected List<InterfaceInfo> interfaceList;

	protected Database database = null;

	protected Map<String, String> sendedRowUpdateColumnValueMap = new HashMap<String, String>();

	protected Map<String, String> returnedResultsUpdateColumnValueMap = new HashMap<String, String>();

	protected Map<String, String> receivedRowsDMLColumnValueMap = new HashMap<String, String>();

	String statusColumnName;

	String failStatusColumnValue;

	String successStatusColumnValue;

	protected Database collectDatabaseMetadata(Connection connection,
			String schemaNamePattern, String tableNamePattern) {
		final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
		options.setSchemaInfoLevel(SchemaInfoLevel.standard());
		options.setTableTypesString("TABLE,VIEW,SYNONYM");
		options.setSchemaInclusionRule(new InclusionRule(schemaNamePattern,
				InclusionRule.NONE));
		options.setTableInclusionRule(new InclusionRule(tableNamePattern,
				InclusionRule.NONE));
		Database scDatabase = null;
		try {
			scDatabase = SchemaCrawlerUtility.getDatabase(connection, options);
		} catch (SchemaCrawlerException e) {
			logger.error(e);
		}
		return scDatabase;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(interfaceList, "Property 'interfaceList' ");
	}

	public Database getDatabase() {
		return database;
	}

	public String getSchemaNamePattern() {
		return schemaNamePattern;
	}

	public void setSchemaNamePattern(String schemaNamePattern) {
		this.schemaNamePattern = schemaNamePattern;
	}

	public List<InterfaceInfo> getInterfaceList() {
		return interfaceList;
	}

	public void setInterfaceList(List<InterfaceInfo> interfaceList) {
		this.interfaceList = interfaceList;
	}

	@Override
	public Map<String, String> getSendedRowUpdateColumnValueMap() {
		return sendedRowUpdateColumnValueMap;
	}

	public void setSendedRowUpdateColumnValueMap(
			Map<String, String> sendedRowUpdateColumnValueMap) {
		this.sendedRowUpdateColumnValueMap = sendedRowUpdateColumnValueMap;
	}

	@Override
	public Map<String, String> getReturnedResultsUpdateColumnValueMap() {
		return returnedResultsUpdateColumnValueMap;
	}

	public void setReturnedResultsUpdateColumnValueMap(
			Map<String, String> returnedResultsUpdateColumnValueMap) {
		this.returnedResultsUpdateColumnValueMap = returnedResultsUpdateColumnValueMap;
	}

	@Override
	public Map<String, String> getReceivedRowsDMLColumnValueMap() {
		return receivedRowsDMLColumnValueMap;
	}

	public void setReceivedRowsDMLColumnValueMap(
			Map<String, String> receivedRowsDMLColumnValueMap) {
		this.receivedRowsDMLColumnValueMap = receivedRowsDMLColumnValueMap;
	}

	@Override
	public String getStatusColumnName() {
		return statusColumnName;
	}

	public void setStatusColumnName(String statusColumnName) {
		this.statusColumnName = statusColumnName;
	}

	@Override
	public String getFailStatusColumnValue() {
		return failStatusColumnValue;
	}

	public void setFailStatusColumnValue(String failStatusColumnValue) {
		this.failStatusColumnValue = failStatusColumnValue;
	}

	@Override
	public String getSuccessStatusColumnValue() {
		return successStatusColumnValue;
	}

	public void setSuccessStatusColumnValue(String successStatusColumnValue) {
		this.successStatusColumnValue = successStatusColumnValue;
	}

}
