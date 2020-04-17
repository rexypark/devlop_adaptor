package com.indigo.esb.std.db.rcv;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

/**
 * @author clupine
 *
 */
public interface QueryMapper {
	
	@Select(" ${query} ")
	List<Map<String,Object>> getData( Map<String,String> queryMap);

	@Select(" ${query} ")
	void delete( Map<String,String> queryMap);
}
