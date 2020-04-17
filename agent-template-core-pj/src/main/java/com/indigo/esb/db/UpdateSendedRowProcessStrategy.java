package com.indigo.esb.db;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.util.Assert;

public class UpdateSendedRowProcessStrategy implements SendedRowProcessStrategy {

	protected SimpleJdbcTemplate jdbcTemplate;

	public UpdateSendedRowProcessStrategy() {
	}

	public UpdateSendedRowProcessStrategy(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = new SimpleJdbcTemplate(jdbcTemplate);
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = new SimpleJdbcTemplate(jdbcTemplate);
	}

	@Override
	public int[] processSendedRows(List<Map<String, Object>> resultList) {
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(jdbcTemplate, "Property 'jdbcTemplate' is Required");
	}

}
