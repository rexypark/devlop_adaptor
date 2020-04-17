package com.indigo.esb.std.p05;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.indigo.esb.adaptor.strategy.db.OnMessageDBSupport;
import com.indigo.esb.db.QueryProvider;

public abstract class P05OnMessageDBSupport extends OnMessageDBSupport{
	
	protected QueryProvider queryProvider;
	
	protected JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings("deprecation")
	protected SimpleJdbcTemplate sjt;

}
