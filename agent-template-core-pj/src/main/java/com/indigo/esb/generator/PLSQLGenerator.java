package com.indigo.esb.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
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

/**
 * #444 관련 프로시져 스크립트 생성 프로그램
 * @author yoonjonghoon 
 *
 */
public class PLSQLGenerator { 

	private static final Log logger = LogFactory
			.getLog(PLSQLGenerator.class);

	VelocityEngine ve;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		PLSQLGenerator gen = new PLSQLGenerator();
		gen.generateQuery();
	}

	public PLSQLGenerator() throws Exception {
		ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		ve.setProperty("file.resource.loader.class",
				FileResourceLoader.class.getName());
		ve.setProperty("input.encoding", "utf-8");
		ve.setProperty("output.encoding", "utf-8");
		// ve.setProperty("file.resource.loader.path", "./templates");
	}

	public void generateQuery() throws ResourceNotFoundException,
			ParseErrorException, Exception {
		Connection connection = null;
		try {
			DataSource dataSource = new DatabaseConnectionOptions(
					"oracle.jdbc.OracleDriver",
					"jdbc:oracle:thin:@localhost:1521:XE");
			connection = dataSource.getConnection("usituation", "usituation");
		} catch (SchemaCrawlerException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String schemaNamePattern = "USITUATION";
		String tableNamePattern = ".*\\.INCIDENT_LOC";
//		String tableNamePattern = ".*\\.INCIDENT_LOC||.*\\.RAGINGBULL_INFO";

		final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
		options.setSchemaInfoLevel(SchemaInfoLevel.standard());
		options.setTableTypesString("TABLE,VIEW,SYNONYM");
		options.setSchemaInclusionRule(new InclusionRule(schemaNamePattern,
				InclusionRule.NONE));
		options.setTableInclusionRule(new InclusionRule(tableNamePattern,
				InclusionRule.NONE));

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
		logger.info(database);
		for (final Schema schema : database.getSchemas()) {
			logger.info(schema);
			for (final Table table : schema.getTables()) {
				logger.info("테이블명o--> " + table.getName());
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
				// printConfigXml(schema, table);
				generateConfigXml(schema, table);
			}// end of tables
		}// end of schemas
	}

	private void printConfigXml(Schema schema, Table table)
			throws ResourceNotFoundException, ParseErrorException, Exception {
		Context context = new VelocityContext();
		context.put("schema", schema);
		context.put("table", table);
		context.put("table_name", table.getName());

		Writer writer = new PrintWriter(System.out, true);
		Template template = ve.getTemplate("ibatis_sqlmap.vm");
		template.merge(context, writer);
		writer.close();
	}

	private void generateConfigXml(Schema schema, Table table)
			throws ResourceNotFoundException, ParseErrorException, Exception {
		Context context = new VelocityContext();
		context.put("schema", schema);
		context.put("table", table);
		logger.info("테이블명:"+table.getName());
		context.put("table_name", table.getName());
		
		String outFileName = new File(new File("./templates/output"),
				"PR_"+table.getName() + ".sql").getAbsolutePath();
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				outFileName)));

		Template template = ve.getTemplate("IF2APP_procedure.vm", "utf-8");
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

}
