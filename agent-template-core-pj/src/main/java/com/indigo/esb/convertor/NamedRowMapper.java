package com.indigo.esb.convertor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecacet.jflat.ReaderRowMapper;

public class NamedRowMapper implements ReaderRowMapper<Map>{
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	Set<String> nameSet;

	public NamedRowMapper(Set<String> nameSet) {
		this.nameSet = nameSet;
	}
	
	@Override
	public Map getRow(String[] row, int rowNumber) {
		try{
			Map rowMap = new LinkedHashMap();
			int idx = 0; 
			
			for (String name : nameSet) {
				rowMap.put(name, row[idx++]);
			}
			return rowMap;
		}catch(Exception e){
			logger.error("NamedRowMapper Error , ",e);
			return null;
		}
		
	}

}
