package com.indigo.esb.db.query;

import java.util.Map;

import schemacrawler.schema.Column;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;

import com.indigo.esb.db.SchemaCrawlerUtil;

public class HSQLQueryMaker extends SchemaCrawlerQueryMaker {

	@Override
	public String makeSelectString(Table table, String where, String orderby, int rowCnt) {
		StringBuilder sSql = new StringBuilder();
		sSql.append("SELECT TOP " + rowCnt + " ");
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

	// CREATE TABLE B(ID INT UNIQUE, A_ID INT);
	//
	// MERGE INTO B
	// USING (VALUES 2, 3) I (ID, A_ID)
	// ON (B.ID=I.ID)
	// WHEN MATCHED THEN UPDATE SET B.A_ID = I.A_ID
	// WHEN NOT MATCHED THEN INSERT (ID, A_ID) VALUES (I.ID, I.A_ID)
	@Override
	public String makeMergeString(Table table, Map<String, String> rcvMColValMap) {
		StringBuilder mSql = new StringBuilder();
		mSql.append("MERGE INTO ");
		mSql.append(table.getName());
		mSql.append(" USING (VALUES ");
		PrimaryKey pk = table.getPrimaryKey();
		IndexColumn[] pkCols = pk.getColumns();
		for (int i = 0; i < pkCols.length; i++) {
			if (i > 0) {
				mSql.append(", ");
			}
			mSql.append(":");
			mSql.append(pkCols[i].getName());
		}
		mSql.append(" ) I (");
		for (int i = 0; i < pkCols.length; i++) {
			if (i > 0) {
				mSql.append(", ");
			}
			mSql.append(pkCols[i].getName());
		}
		mSql.append(" ) ON (");
		for (int i = 0; i < pkCols.length; i++) {
			if (i > 0) {
				mSql.append(" AND ");
			}
			mSql.append(table.getName() + "." + pkCols[i].getName());
			mSql.append("=I.");
			mSql.append(pkCols[i].getName());
		}
		mSql.append(" )");
		mSql.append(" WHEN MATCHED THEN ");
		mSql.append(" UPDATE SET ");
		Column[] cols = table.getColumns();
		int k = 0;
		for (int i = 0; i < cols.length; i++) {
			if (SchemaCrawlerUtil.isPKColumn(cols[i].getName(), pkCols)) {
				continue;
			}
			if (k > 0) {
				mSql.append(", ");
			}
			k++;
			mSql.append(cols[i].getName());
			mSql.append(" =");
			if (rcvMColValMap.containsKey(cols[i].getName())) {
				mSql.append(rcvMColValMap.get(cols[i].getName()));
			} else {
				mSql.append(" :");
				mSql.append(cols[i].getName());
			}
		}
		mSql.append(" WHEN NOT MATCHED THEN ");
		mSql.append(" INSERT (");
		mSql.append(table.getColumnsListAsString());
		mSql.append(" )");
		mSql.append(" VALUES (");
		for (int i = 0; i < cols.length; i++) {
			if (i > 0) {
				mSql.append(", ");
			}
			if (rcvMColValMap.containsKey(cols[i].getName())) {
				mSql.append(rcvMColValMap.get(cols[i].getName()));
			} else {
				mSql.append(" :");
				mSql.append(cols[i].getName());
			}
		}
		mSql.append(" )");

		return mSql.toString();

	}
}
