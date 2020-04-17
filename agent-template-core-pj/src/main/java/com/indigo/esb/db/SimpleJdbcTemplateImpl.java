package com.indigo.esb.db;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class SimpleJdbcTemplateImpl {

	private static SimpleJdbcTemplate simpleJdbcTemplate;
	
	public static SimpleJdbcTemplate getSimpleJdbcTemplate(DataSource dataSource) {
		if(simpleJdbcTemplate == null){
			simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		}
		return simpleJdbcTemplate; 
	}
}
