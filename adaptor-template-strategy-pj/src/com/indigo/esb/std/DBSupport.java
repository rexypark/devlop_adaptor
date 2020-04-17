package com.indigo.esb.std;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service("DBSupport")
public class DBSupport {

 @Autowired(required = false)
 @Qualifier("sqlSession")
 protected SqlSessionTemplate sqlSession;

 @Autowired
 ApplicationContext applicationContext;

 public DBTransactionManager getTransactionManager() {
  return applicationContext.getBean(DBTransactionManager.class);
 }
}