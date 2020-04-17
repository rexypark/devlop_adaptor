package com.indigo.esb.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import schemacrawler.schema.Database;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

public class SchemaCrawlerUtil {
	private static final Log logger = LogFactory.getLog(SchemaCrawlerUtil.class);

	public static boolean isPKColumn(String colName, IndexColumn[] pkCols) {
		for (IndexColumn ic : pkCols) {
			if (colName.equalsIgnoreCase(ic.getName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isPkColumn(Database db, String colName, String targetTableName) {
		for (final Schema schema : db.getSchemas()) {
			for (final Table table : schema.getTables()) {
				if (targetTableName.equalsIgnoreCase(table.getName())) {
					PrimaryKey pk = table.getPrimaryKey();
					IndexColumn[] pkCols = pk.getColumns();
					return isPKColumn(colName, pkCols);
				}
			}
		}
		return false;
	}
}
