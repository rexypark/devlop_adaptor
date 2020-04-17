package com.indigo.esb.db.query;

import java.util.Map;

import schemacrawler.schema.Table;

public interface QueryMaker {

	String makeSelectString(Table table, String where, String orderby, int rowCnt);

	String makeSourceUpdate(Table table, Map<String, String> sendedRowUpdateColumnValueMap);

	String makeInsertString(Table table, Map<String, String> esbInsertValueMap);

	String makeSourceResultUpdate(Table table, Map<String, String> rsltUColValMap);

	String makeUpdateString(Table table, Map<String, String> rcvUColValMap);

	String makeDeleteString(Table table);

	String makeMergeString(Table table, Map<String, String> rcvMColValMap);

}
