package com.indigo.esb.adaptor.strategy.db;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.strategy.OnSignalStrategy;
import com.indigo.esb.db.QueryStatementType;

public abstract class OnSignalSpacenameDBSupport implements OnSignalStrategy, InitializingBean {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired(required = false)
	protected JmsTemplate jmsTemplate;
	@Autowired(required = false)
	protected SqlSessionTemplate sqlSession;
	
	@Autowired(required = false )
	protected SqlSessionTemplate sqlSession_tgt;

	protected String getMybaitsSqlId(String interfaceId, QueryStatementType insert) {
		StringBuffer sb = new StringBuffer();
		sb.append(interfaceId);
		sb.append(".");
		sb.append(insert);
		return sb.toString();
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {

	}

}
