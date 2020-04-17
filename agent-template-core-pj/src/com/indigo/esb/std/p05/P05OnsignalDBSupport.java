package com.indigo.esb.std.p05;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.indigo.esb.adaptor.strategy.db.OnSignalDBSupport;
import com.indigo.esb.db.QueryProvider;

@SuppressWarnings("deprecation")
public abstract class P05OnsignalDBSupport extends OnSignalDBSupport{

	protected QueryProvider queryProvider;
	
	protected JdbcTemplate jdbcTemplate;
	
	protected SimpleJdbcTemplate sjt;
	
	
	
	
	public void setQueryProvider(QueryProvider queryProvider) {
		this.queryProvider = queryProvider;
	}


	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.sjt = new SimpleJdbcTemplate(jdbcTemplate.getDataSource());
	}

}
