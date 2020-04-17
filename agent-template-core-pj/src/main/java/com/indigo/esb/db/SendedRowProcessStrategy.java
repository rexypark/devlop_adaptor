package com.indigo.esb.db;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

public interface SendedRowProcessStrategy extends InitializingBean{

	int[] processSendedRows(List<Map<String,Object>> resultList);
}
