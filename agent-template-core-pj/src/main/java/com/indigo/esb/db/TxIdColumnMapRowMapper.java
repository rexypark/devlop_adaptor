package com.indigo.esb.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.ColumnMapRowMapper;

public class TxIdColumnMapRowMapper extends ColumnMapRowMapper {

	final String txId;

	final String txIdColumnName;

	public TxIdColumnMapRowMapper() {
		this("TX_ID");
	}

	public TxIdColumnMapRowMapper(String txIdColumnName) {
		this.txIdColumnName = txIdColumnName;
		this.txId = UUID.randomUUID().toString();
	}

	@Override
	public Map<String, Object> mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		Map<String, Object> map = super.mapRow(rs, rowNum);
		map.put(txIdColumnName, txId);
		return map;
	}
	
	@Override
	protected Map<String, Object> createColumnMap(int columnCount) {
		return  new LinkedHashMap<String,Object>(columnCount);
	}
	
	public String getTxId() {
		return txId;
	}
}
