package com.indigo.esb.std.db.relational.common;

public class SqlUtil {
	public static String getMybaitsSqlId(String interfaceId, PLCQueryStatementType type) {
		StringBuffer sb = new StringBuffer();
		sb.append(interfaceId);
		sb.append(".");
		sb.append(type);
		return sb.toString();
	}
	
	public static String getSubTableSqlId(String interfaceId, String subTableName, PLCQueryStatementType type) {
		StringBuffer sb = new StringBuffer();
		sb.append(interfaceId);
		sb.append(".");
		sb.append(subTableName);
		sb.append("_");
		sb.append(type);
		return sb.toString();
	}
}
