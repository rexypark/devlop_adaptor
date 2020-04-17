package com.indigo.esb.db;

import java.util.Map;
import java.util.UUID;

import com.ibatis.sqlmap.engine.mapping.statement.DefaultRowHandler;

public class TxIdRowHandler extends DefaultRowHandler {

	final String txId;
	
	final String txIdColumnName;
	
	public TxIdRowHandler() {
		this("TX_ID");
	}

	public TxIdRowHandler(String txIdColumnName) {
		this.txIdColumnName = txIdColumnName;
		this.txId = UUID.randomUUID().toString();
	}

	@Override
	public void handleRow(Object valueObject) {
		Map map = (Map)valueObject;
		map.put(txIdColumnName, txId);
		super.handleRow(map);
	}
	
	public String getTxId() {
		return txId;
	}

}
