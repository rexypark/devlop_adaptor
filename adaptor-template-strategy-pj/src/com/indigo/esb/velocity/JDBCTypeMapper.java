package com.indigo.esb.velocity;

import java.util.HashMap;
import java.util.Map;

public class JDBCTypeMapper {
	
	static Map<String,String> type;
	
	static{
		type = new HashMap<String,String>();
		type.put("VARCHAR2", "VARCHAR");
		type.put("CHAR", "CHAR");
		type.put("NUMBER", "DECIMAL");
		type.put("DATE", "TIMESTAMP");
	}
	
	public static String getType(String jdbcType){
		return type.get(jdbcType);
	}

}
