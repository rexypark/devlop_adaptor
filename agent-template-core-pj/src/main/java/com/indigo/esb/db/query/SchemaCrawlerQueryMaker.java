package com.indigo.esb.db.query;

import java.util.Map;

import com.indigo.esb.db.SchemaCrawlerUtil;

import schemacrawler.schema.Column;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;

public class SchemaCrawlerQueryMaker implements QueryMaker {

	public SchemaCrawlerQueryMaker() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indigo.esb.db.QueryMaker#makeSelectString(schemacrawler.schema.Table,
	 * java.lang.String, java.lang.String, int)
	 */

	@Override
	public String makeSelectString(Table table, String where, String orderby,
			int rowCnt) {
		StringBuilder sSql = new StringBuilder();
		sSql.append("SELECT * FROM ( SELECT ");
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
		sSql.append(" ) WHERE ROWNUM <= " + rowCnt);

		return sSql.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indigo.esb.db.QueryMaker#makeSourceUpdate(schemacrawler.schema.Table,
	 * java.util.Map)
	 */
	@Override
	public String makeSourceUpdate(Table table,
			Map<String, String> sendedRowUpdateColumnValueMap) {
		StringBuilder sUSql = new StringBuilder();
		sUSql.append("UPDATE ");
		sUSql.append(table.getName());
		sUSql.append(" SET ");
		int i = 0;
		for (Map.Entry<String, String> esbColumnEntry : sendedRowUpdateColumnValueMap
				.entrySet()) {
			if (i > 0) {
				sUSql.append(", ");
			}
			sUSql.append(esbColumnEntry.getKey());
			sUSql.append(" =");
			sUSql.append(esbColumnEntry.getValue());
			i++;
		}
		sUSql.append(" WHERE ");
		PrimaryKey pk = table.getPrimaryKey();
		IndexColumn[] pkCols = pk.getColumns();
		for (int k = 0; k < pkCols.length; k++) {
			if (k > 0) {
				sUSql.append(" AND ");
			}
			sUSql.append(pkCols[k].getName());
			sUSql.append("=:");
			sUSql.append(pkCols[k].getName());
		}
		return sUSql.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indigo.esb.db.QueryMaker#makeInsertString(schemacrawler.schema.Table,
	 * java.util.Map)
	 */
	@Override
	public String makeInsertString(Table table,
			Map<String, String> esbInsertValueMap) {
		StringBuilder iSql = new StringBuilder();
		iSql.append("INSERT INTO ");
		iSql.append(table.getName());
		iSql.append(" (");
		iSql.append(table.getColumnsListAsString());
		iSql.append(") VALUES(");
		Column[] cols = table.getColumns();
		for (int i = 0; i < cols.length; i++) {
			if (i > 0) {
				iSql.append(", ");
			}
			if (esbInsertValueMap.containsKey(cols[i].getName())) {
				iSql.append(esbInsertValueMap.get(cols[i].getName()));
			} else {
				iSql.append(":");
				iSql.append(cols[i].getName());
			}
		}
		iSql.append(")");
		return iSql.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indigo.esb.db.QueryMaker#makeSourceResultUpdate(schemacrawler.schema
	 * .Table, java.util.Map)
	 */
	@Override
	public String makeSourceResultUpdate(Table table,
			Map<String, String> rsltUColValMap) {
		StringBuilder rsltUSql = new StringBuilder();
		rsltUSql.append("UPDATE ");
		rsltUSql.append(table.getName());
		rsltUSql.append(" SET ");
		int idx = 0;
		for (Map.Entry<String, String> colValEntry : rsltUColValMap.entrySet()) {
			if (idx > 0) {
				rsltUSql.append(", ");
			}
			idx++;
			rsltUSql.append(colValEntry.getKey());
			rsltUSql.append(" =");
			rsltUSql.append(colValEntry.getValue());
		}
		rsltUSql.append(" WHERE ");
		PrimaryKey pk = table.getPrimaryKey();
		IndexColumn[] pkCols = pk.getColumns();
		for (int i = 0; i < pkCols.length; i++) {
			if (i > 0) {
				rsltUSql.append(" AND ");
			}
			rsltUSql.append(pkCols[i].getName());
			rsltUSql.append("=:");
			rsltUSql.append(pkCols[i].getName());
		}
		return rsltUSql.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indigo.esb.db.QueryMaker#makeUpdateString(schemacrawler.schema.Table,
	 * java.util.Map)
	 */
	@Override
	public String makeUpdateString(Table table,
			Map<String, String> rcvUColValMap) {
		StringBuilder rcvUSql = new StringBuilder();
		rcvUSql.append("UPDATE ");
		rcvUSql.append(table.getName());
		rcvUSql.append(" SET ");
		Column[] cols = table.getColumns();
		for (int i = 0; i < cols.length; i++) {
			if (i > 0) {
				rcvUSql.append(", ");
			}
			rcvUSql.append(cols[i].getName());
			rcvUSql.append(" =");
			if (rcvUColValMap.containsKey(cols[i].getName())) {
				rcvUSql.append(rcvUColValMap.get(cols[i].getName()));
			} else {
				rcvUSql.append(":");
				rcvUSql.append(cols[i].getName());
			}
		}
		rcvUSql.append(" WHERE ");
		PrimaryKey pk = table.getPrimaryKey();
		IndexColumn[] pkCols = pk.getColumns();
		for (int i = 0; i < pkCols.length; i++) {
			if (i > 0) {
				rcvUSql.append(" AND ");
			}
			rcvUSql.append(pkCols[i].getName());
			rcvUSql.append("=:");
			rcvUSql.append(pkCols[i].getName());
		}
		return rcvUSql.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indigo.esb.db.QueryMaker#makeDeleteString(schemacrawler.schema.Table)
	 */
	@Override
	public String makeDeleteString(Table table) {
		StringBuilder rcvDSql = new StringBuilder();
		rcvDSql.append("DELETE ");
		rcvDSql.append(table.getName());
		rcvDSql.append(" WHERE ");
		PrimaryKey pk = table.getPrimaryKey();
		IndexColumn[] pkCols = pk.getColumns();
		for (int i = 0; i < pkCols.length; i++) {
			if (i > 0) {
				rcvDSql.append(" AND ");
			}
			rcvDSql.append(pkCols[i].getName());
			rcvDSql.append("=:");
			rcvDSql.append(pkCols[i].getName());
		}
		return rcvDSql.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indigo.esb.db.QueryMaker#makeMergeString(schemacrawler.schema.Table,
	 * java.util.Map)
	 */
	@Override
	public String makeMergeString(Table table, Map<String, String> rcvMColValMap) {
		StringBuilder mSql = new StringBuilder();
		mSql.append("MERGE INTO ");
		mSql.append(table.getName());
		mSql.append(" USING DUAL ");
		mSql.append(" ON (");
		PrimaryKey pk = table.getPrimaryKey();
		IndexColumn[] pkCols = pk.getColumns();
		for (int i = 0; i < pkCols.length; i++) {
			if (i > 0) {
				mSql.append(" AND ");
			}
			mSql.append(pkCols[i].getName());
			mSql.append("=:");
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
				mSql.append(":");
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
				mSql.append(":");
				mSql.append(cols[i].getName());
			}
		}
		mSql.append(" )");

		return mSql.toString();
	}

}
