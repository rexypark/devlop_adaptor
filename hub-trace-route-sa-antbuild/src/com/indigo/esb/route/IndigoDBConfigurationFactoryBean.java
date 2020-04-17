package com.indigo.esb.route;

import java.util.Properties;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author $id$ property 테이블에 있는 property 정보를 java.util.properties 객체로 반환한다.
 */
public class IndigoDBConfigurationFactoryBean implements InitializingBean, FactoryBean<Properties>  {

	private CompositeConfiguration configuration;
	private boolean throwExceptionOnMissing = true;
	public SqlSession sqlSession;

	public void afterPropertiesSet() throws Exception {
		this.configuration = new CompositeConfiguration();
		configuration.setThrowExceptionOnMissing(throwExceptionOnMissing);
		Configuration databaseConfiguration = new DatabaseConfiguration(getSqlSession());
		this.configuration.addConfiguration(databaseConfiguration);
	}
	
	@Override
	public Class<?> getObjectType() {
		return java.util.Properties.class;
	}

	public boolean isThrowExceptionOnMissing() {
		return throwExceptionOnMissing;
	}

	public void setThrowExceptionOnMissing(boolean throwExceptionOnMissing) {
		this.throwExceptionOnMissing = throwExceptionOnMissing;
	}

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	public SqlSession getSqlSession() {
		return sqlSession;
	}

	@Override
	public Properties getObject() throws Exception {
		return (configuration != null) ? ConfigurationConverter.getProperties(configuration) : null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}



	

}
