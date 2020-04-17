package com.indigo.esb.velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

public class StandardXmlMapperGenerator {

	private static final Log logger = LogFactory.getLog(StandardXmlMapperGenerator.class);

	VelocityEngine ve;

	String type;
	String jdbcName;
	String jdbcUrl;
	String id;
	String pw;
	String tableName;
	String schemaName;
	String successCode;
	String failureCode;
	String processingCode;
	String ifid;
	String procedure;
	String stateColumn;
	String txidColumn;
	String dateColumn;
	String messageColumn;

	
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public String getSuccessCode() {
		return successCode;
	}

	public void setSuccessCode(String successCode) {
		this.successCode = successCode;
	}

	public String getFailureCode() {
		return failureCode;
	}

	public void setFailureCode(String failureCode) {
		this.failureCode = failureCode;
	}

	public String getProcessingCode() {
		return processingCode;
	}

	public void setProcessingCode(String processingCode) {
		this.processingCode = processingCode;
	}

	public String getIfid() {
		return ifid;
	}

	public void setIfid(String ifid) {
		this.ifid = ifid;
	}

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	public String getStateColumn() {
		return stateColumn;
	}

	public void setStateColumn(String stateColumn) {
		this.stateColumn = stateColumn;
	}

	public String getTxidColumn() {
		return txidColumn;
	}

	public void setTxidColumn(String txidColumn) {
		this.txidColumn = txidColumn;
	}

	public String getDateColumn() {
		return dateColumn;
	}

	public void setDateColumn(String dateColumn) {
		this.dateColumn = dateColumn;
	}

	public String getMessageColumn() {
		return messageColumn;
	}

	public void setMessageColumn(String messageColumn) {
		this.messageColumn = messageColumn;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getJdbcName() {
		return jdbcName;
	}

	public void setJdbcName(String jdbcName) {
		this.jdbcName = jdbcName;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public StandardXmlMapperGenerator() throws Exception {
		ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
		ve.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
		ve.setProperty("input.encoding", "utf-8");
		ve.setProperty("output.encoding", "utf-8");
	}

	public void generateQuery() throws ResourceNotFoundException, ParseErrorException, Exception {

		Connection connection = null;
		try {
			DataSource dataSource = new DatabaseConnectionOptions(jdbcName, jdbcUrl);
			connection = dataSource.getConnection(id, pw);
		} catch (SchemaCrawlerException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String schemaNamePattern = schemaName;
		String tableNamePattern = ".*\\." + tableName;

		final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
		options.setSchemaInfoLevel(SchemaInfoLevel.standard());
		options.setTableTypesString("TABLE,VIEW,SYNONYM");
		options.setSchemaInclusionRule(new InclusionRule(schemaNamePattern, InclusionRule.NONE));
		options.setTableInclusionRule(new InclusionRule(tableNamePattern, InclusionRule.NONE));

		Database database = null;
		try {
			database = SchemaCrawlerUtility.getDatabase(connection, options);
		} catch (SchemaCrawlerException e) {
			logger.error(e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		for (final Schema schema : database.getSchemas()) {
			logger.info(schema);
			for (final Table table : schema.getTables()) {
				logger.info("o--> " + table);
				for (final Column column : table.getColumns()) {
					logger.info("     o--> " + column+"," + column.getType());
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

				Table[] tables = table.getRelatedTables(TableRelationshipType.child);
				for (final Table child : tables) {
					logger.info("o-->Child Table:" + child);
				}
				logger.info("");
				
				if(type.equalsIgnoreCase("SOURCE")){
					genSource(schema, table);
				}else{
					genTarget(schema, table);
				}
			}// end of tables
		}// end of schemas
	}

//	private void printConfigXml(Schema schema, Table table) throws ResourceNotFoundException, ParseErrorException, Exception {
//		
//		Context context = new VelocityContext();
//		context.put("schema", schema);
//		context.put("table", table);
//
//		Writer writer = new PrintWriter(System.out, true);
//		Template template = ve.getTemplate("stdSourceSQL.vm");
//		template.merge(context, writer);
//		writer.close();
//	}

	private void genSource(Schema schema, Table table) throws ResourceNotFoundException, ParseErrorException, Exception {
		
		List<Map<String,String>> pkList = new ArrayList<Map<String,String>>();
		
		IndexColumn[] pk = table.getPrimaryKey().getColumns();
		for (int i = 0; i < pk.length; i++) {
			Map<String,String> pkMap = new HashMap<String,String>();
			pkMap.put("NAME", pk[i].getName());
			pkMap.put("TYPE", JDBCTypeMapper.getType(pk[i].getType().getName()));
//			logger.info("pkMap ==> " + pkMap.toString());
			pkList.add(pkMap);
		}
		
		Context context = new VelocityContext();
		context.put("schema", schema);
		context.put("table", table);
		context.put("jdbcName", jdbcName);
		context.put("jdbcUrl", jdbcUrl);
		context.put("id", id);
		context.put("pw", pw);
		context.put("tableName", tableName);
		context.put("schemaName", schemaName);
		context.put("successCode", successCode);
		context.put("failureCode", failureCode);
		context.put("processingCode", processingCode);
		context.put("ifid", ifid);
		context.put("procedure", procedure);
		context.put("stateColumn", stateColumn);
		context.put("txidColumn", txidColumn);
		context.put("dateColumn", dateColumn);
		context.put("pkList", pkList);
		context.put("messageColumn", messageColumn);

		String outFileName = new File(new File("templates/output/src/"), "SQL_" + ifid + ".xml").getAbsolutePath();
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outFileName)));

		Template template = ve.getTemplate("./templates/stdSourceSQL.vm", "utf-8");
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}
	
	private void genTarget(Schema schema, Table table) throws ResourceNotFoundException, ParseErrorException, Exception {
		
		List<Map<String,String>> columnList = new ArrayList<Map<String,String>>();
		
		Column[] column = table.getColumns();
		for (int i = 0; i < column.length; i++) {
			Map<String,String> columnMap = new HashMap<String,String>();
			columnMap.put("NAME", column[i].getName());
			columnMap.put("TYPE", JDBCTypeMapper.getType(column[i].getType().getName()));
			logger.info("columnMap ==> " + columnMap.toString());
			columnList.add(columnMap);
		}
		
		Context context = new VelocityContext();
		context.put("schema", schema);
		context.put("table", table);
		context.put("jdbcName", jdbcName);
		context.put("jdbcUrl", jdbcUrl);
		context.put("id", id);
		context.put("pw", pw);
		context.put("tableName", tableName);
		context.put("schemaName", schemaName);
		context.put("successCode", successCode);
		context.put("failureCode", failureCode);
		context.put("processingCode", processingCode);
		context.put("ifid", ifid);
		context.put("procedure", procedure);
		context.put("stateColumn", stateColumn);
		context.put("txidColumn", txidColumn);
		context.put("dateColumn", dateColumn);
		context.put("columnList", columnList);
		context.put("messageColumn", messageColumn);
		
		String outFileName = new File(new File("templates/output/tgt/"), "SQL_" + ifid + ".xml").getAbsolutePath();
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outFileName)));
		
		Template template = ve.getTemplate("./templates/stdTargetSQL.vm", "utf-8");
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

}
