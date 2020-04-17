package com.indigo.esb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionUtil {

	public static List<Map<String, Object>> makeFilterdMapList(String colNamesStr, List<Map<String, Object>> recvList) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		String[] colNames = null;
		if (colNamesStr != null) {
			colNames = colNamesStr.split(",");
			Map<String, Object> resultMap = null;
			for (Map<String, Object> recordMap : recvList) {
				resultMap = new HashMap<String, Object>();
				for (String colName : colNames) {
					if (recordMap.containsKey(colName)) {
						resultMap.put(colName, recordMap.get(colName));
					}
				}
				resultList.add(resultMap);
			}
		}
		return resultList;
	}
}
