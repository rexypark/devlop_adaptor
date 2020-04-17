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

public class SqlmapClientXmlGenerator {

	private static final Log logger = LogFactory
			.getLog(SqlmapClientXmlGenerator.class);

	VelocityEngine ve;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		SqlmapClientXmlGenerator gen = new SqlmapClientXmlGenerator();
		gen.generateQuery();
	}

	public SqlmapClientXmlGenerator() throws Exception {
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
					"jdbc:oracle:thin:@10.171.106.196:1521:cirsdb1");
			connection = dataSource.getConnection("rrcesb1", "rrcesb43");
		} catch (SchemaCrawlerException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String schemaNamePattern = "RRCESB1";
		String tableNamePattern = ".*\\.IFEFFRRPINQS";

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

		String outFileName = new File(new File("./templates/output"),
				"sqlmap_"+table.getName() + ".xml").getAbsolutePath();
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				outFileName)));

		Template template = ve.getTemplate("ibatis_sqlmap.vm", "utf-8");
		template.merge(context, writer);
		writer.flush();
		writer.close();
	}

}
