package com.indigo.esb.db.ibatis;


public interface SqlMapClientRefreshable {

	void refresh() throws Exception;
	

	void setCheckInterval(int ms);
	
}
