package com.indigo.esb.db.query;

import java.util.Map;

import schemacrawler.schema.Table;

public class MsSqlQueryMaker extends SchemaCrawlerQueryMaker {

	@Override
	public String makeSelectString(Table table, String where, String orderby,
			int rowCnt) {
		StringBuilder sSql = new StringBuilder();
		sSql.append("SELECT TOP " + rowCnt +" ");
		sSql.append(table.getColumnsListAsString());
		sSql.append(" FROM ");
		sSql.append(table.getName());
		if (where != null) {
			sSql.append(" WHERE ");
			sSql.append(where);
		}
		if (orderby != null) {
			sSql.append(" ");
			sSql.append(orderby);
		}
		return sSql.toString();
	}
	
//	-- MERGE INTO TEST
//	C REATE TABLE MERGE_TEST
//	(INT1 INT,
//	INT2 INT,
//	VAL VARCHAR(100) )
//	 
//	MERGE INTO MERGE_TEST MG
//	USING (SELECT 1 AS INT1, 1 AS INT2, 'AAA' AS VAL) A
//	ON (MG.INT1 = A.INT1 AND MG.INT2 = A.INT2)
//	WHEN MATCHED THEN
//	       UPDATE SET
//	             VAL = A.VAL
//	WHEN NOT MATCHED THEN
//	       INSERT
//	       (INT1, INT2, VAL)
//	       VALUES
//	       (A.INT1, A.INT2, A.VAL)
//	;
	@Override
	public String makeMergeString(Table table, Map<String, String> rcvMColValMap) {
		// TODO Auto-generated method stub
		return super.makeMergeString(table, rcvMColValMap);
	}
}
