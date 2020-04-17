package com.indigo.esb.db.query;

import org.springframework.util.Assert;

public class SchemaCrawlerQueryMakerFactory {

	public static QueryMaker createQueryMaker(String dbName) {

		QueryMaker queryMaker = null;
		if ("Microsoft SQL Server".equalsIgnoreCase(dbName)) {
			queryMaker = new MsSqlQueryMaker();
		} else if ("Oracle".equalsIgnoreCase(dbName)) {
			queryMaker = new SchemaCrawlerQueryMaker();
		} else if ("HSQL Database Engine".equalsIgnoreCase(dbName)) {
			queryMaker = new HSQLQueryMaker();
		} else {
			Assert.notNull(queryMaker, "dbName:'" + dbName + "' ============ QueryMaker =============.");
		}
		return queryMaker;
	}

}
